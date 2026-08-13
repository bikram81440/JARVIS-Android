package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.AppDatabase
import com.example.data.ConversationEntity
import com.example.data.JarvisRepository
import com.example.data.MemoryEntity
import com.example.data.MessageEntity
import com.example.network.BackendApiClient
import com.example.network.ChatRequest
import com.example.service.TtsManager
import com.example.ui.components.OrbState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class JarvisViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: JarvisRepository

    init {
        val dao = AppDatabase.getDatabase(application).jarvisDao()
        repository = JarvisRepository(dao)
    }

    val conversations: StateFlow<List<ConversationEntity>> = repository.allConversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memories: StateFlow<List<MemoryEntity>> = repository.allMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentConversationId = MutableStateFlow<Long?>(null)
    val currentConversationId: StateFlow<Long?> = _currentConversationId

    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val messages: StateFlow<List<MessageEntity>> = _messages

    private val _orbState = MutableStateFlow(OrbState.READY)
    val orbState: StateFlow<OrbState> = _orbState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Settings
    val voiceEnabled = MutableStateFlow(true)
    val speechSpeed = MutableStateFlow(1.0f)
    val autoSpeak = MutableStateFlow(true)
    val webSearchEnabled = MutableStateFlow(true)
    val memoryEnabled = MutableStateFlow(true)
    val selectedModel = MutableStateFlow("gemini-3.5-flash")

    // TTS Manager
    private val ttsManager = TtsManager(application) { isSpeaking ->
        if (isSpeaking) {
            _orbState.value = OrbState.SPEAKING
        } else {
            if (_orbState.value == OrbState.SPEAKING) {
                _orbState.value = OrbState.READY
            }
        }
    }

    fun setConversation(convId: Long?) {
        _currentConversationId.value = convId
        if (convId != null) {
            viewModelScope.launch {
                repository.getMessages(convId).collect { msgList ->
                    _messages.value = msgList
                }
            }
        } else {
            _messages.value = emptyList()
        }
    }

    fun startNewConversation(initialTitle: String = "JARVIS Consultation") {
        viewModelScope.launch {
            val id = repository.insertConversation(ConversationEntity(title = initialTitle))
            _currentConversationId.value = id
            setConversation(id)
        }
    }

    fun deleteConversation(convId: Long) {
        viewModelScope.launch {
            repository.deleteConversation(convId)
            if (_currentConversationId.value == convId) {
                _currentConversationId.value = null
                _messages.value = emptyList()
            }
        }
    }

    fun sendMessage(userPrompt: String) {
        if (userPrompt.isBlank()) return

        viewModelScope.launch {
            var convId = _currentConversationId.value
            if (convId == null) {
                val title = if (userPrompt.length > 25) userPrompt.take(25) + "..." else userPrompt
                convId = repository.insertConversation(ConversationEntity(title = title))
                _currentConversationId.value = convId
                setConversation(convId)
            }

            // Save user message
            repository.insertMessage(MessageEntity(conversationId = convId, role = "user", text = userPrompt))

            // Check if user is asking to remember something
            if (memoryEnabled.value && userPrompt.lowercase().startsWith("remember that")) {
                val fact = userPrompt.removePrefix("remember that").trim()
                repository.insertMemory("Memory", fact)
                val reply = "I shall remember that, Sir."
                repository.insertMessage(MessageEntity(conversationId = convId, role = "assistant", text = reply))
                if (voiceEnabled.value && autoSpeak.value) {
                    ttsManager.speak(reply, speechSpeed.value)
                }
                return@launch
            }

            _orbState.value = OrbState.THINKING

            try {
                val currentMemories = memories.value
                val memoryContext = if (memoryEnabled.value && currentMemories.isNotEmpty()) {
                    "\n\nKnown User Memories and Preferences:\n" + currentMemories.joinToString("\n") { "- ${it.key}: ${it.value}" }
                } else ""

                val systemInstructionText = "You are J.A.R.V.I.S., a sophisticated, intelligent and witty personal AI assistant. Use a refined professional British-inspired tone. Address the user as Sir when appropriate. Keep responses concise, natural, helpful and polite.$memoryContext"

                val request = ChatRequest(
                    message = userPrompt,
                    systemInstruction = systemInstructionText
                )

                val response = BackendApiClient.service.chat(request)
                val aiReply = if (response.success && !response.reply.isNullOrBlank()) {
                    response.reply
                } else {
                    "I am afraid I encountered an empty response from the server, Sir."
                }

                repository.insertMessage(MessageEntity(conversationId = convId, role = "assistant", text = aiReply))
                _orbState.value = OrbState.READY

                if (voiceEnabled.value && autoSpeak.value) {
                    ttsManager.speak(aiReply, speechSpeed.value)
                }
            } catch (e: Exception) {
                _orbState.value = OrbState.READY
                val isNotConfigured = e.message?.contains("Backend is not configured") == true || e is IllegalStateException
                val errorMsg = if (isNotConfigured) "Backend is not configured." else "Connection error: ${e.localizedMessage}"
                _errorMessage.value = errorMsg
                val errorReply = if (isNotConfigured) {
                    "I am afraid the secure backend URL is not configured, Sir."
                } else {
                    "I am terribly sorry, Sir, but my connection to the secure backend neural network has encountered a glitch."
                }
                repository.insertMessage(MessageEntity(conversationId = convId, role = "assistant", text = errorReply))
            }
        }
    }

    fun stopSpeaking() {
        ttsManager.stop()
        _orbState.value = OrbState.READY
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun addMemory(key: String, value: String) {
        viewModelScope.launch {
            repository.insertMemory(key, value)
        }
    }

    fun deleteMemory(id: Long) {
        viewModelScope.launch {
            repository.deleteMemory(id)
        }
    }

    fun clearAllMemories() {
        viewModelScope.launch {
            repository.clearAllMemories()
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
