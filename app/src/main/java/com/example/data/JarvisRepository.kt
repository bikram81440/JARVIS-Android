package com.example.data

import kotlinx.coroutines.flow.Flow

class JarvisRepository(private val dao: JarvisDao) {
    val allConversations: Flow<List<ConversationEntity>> = dao.getAllConversations()
    val allMemories: Flow<List<MemoryEntity>> = dao.getAllMemories()

    fun getMessages(conversationId: Long): Flow<List<MessageEntity>> = dao.getMessagesForConversation(conversationId)

    suspend fun insertConversation(conversation: ConversationEntity): Long = dao.insertConversation(conversation)
    suspend fun deleteConversation(conversationId: Long) {
        dao.deleteMessagesForConversation(conversationId)
        dao.deleteConversation(conversationId)
    }

    suspend fun insertMessage(message: MessageEntity): Long = dao.insertMessage(message)

    suspend fun insertMemory(key: String, value: String) {
        dao.insertMemory(MemoryEntity(key = key, value = value))
    }

    suspend fun deleteMemory(memoryId: Long) = dao.deleteMemory(memoryId)
    suspend fun clearAllMemories() = dao.clearAllMemories()
}
