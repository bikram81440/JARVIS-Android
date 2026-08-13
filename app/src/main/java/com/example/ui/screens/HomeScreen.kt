package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.service.DeviceActionHandler
import com.example.ui.components.AiOrb
import com.example.ui.components.OrbState
import com.example.ui.theme.JarvisCard
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisNavy
import com.example.ui.theme.JarvisSurface
import com.example.viewmodel.JarvisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: JarvisViewModel,
    onNavigateChat: () -> Unit,
    onNavigateHistory: () -> Unit,
    onNavigateMemory: () -> Unit,
    onNavigateFiles: () -> Unit,
    onNavigateSettings: () -> Unit
) {
    val context = LocalContext.current
    val orbState by viewModel.orbState.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var textInput by remember { mutableStateOf("") }
    var showSearchDialog by remember { mutableStateOf(false) }
    var searchQueryText by remember { mutableStateOf("") }

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = matches?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                viewModel.sendMessage(spokenText)
                onNavigateChat()
            }
        }
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to JARVIS, Sir...")
            }
            try {
                speechRecognizerLauncher.launch(intent)
            } catch (e: Exception) {
                viewModel.sendMessage("Greetings JARVIS, status report.")
                onNavigateChat()
            }
        } else {
            viewModel.sendMessage("Microphone permission denied, Sir.")
            onNavigateChat()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SmartToy, contentDescription = null, tint = JarvisCyan, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("J.A.R.V.I.S.", fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateHistory, modifier = Modifier.testTag("history_button")) {
                        Icon(Icons.Default.History, contentDescription = "History", tint = Color.White)
                    }
                    IconButton(onClick = onNavigateSettings, modifier = Modifier.testTag("settings_button")) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = JarvisNavy)
            )
        },
        containerColor = JarvisNavy
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(errorMessage ?: "", color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.clearError() }) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss")
                        }
                    }
                }
            }

            // AI Orb
            Spacer(modifier = Modifier.height(16.dp))
            AiOrb(
                state = orbState,
                onClick = {
                    if (orbState == OrbState.SPEAKING) {
                        viewModel.stopSpeaking()
                    } else {
                        viewModel.sendMessage("At your service, Sir. How may I assist?")
                    }
                },
                modifier = Modifier.testTag("ai_orb")
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "STATUS: ${orbState.name}",
                color = JarvisCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton(icon = Icons.Default.Search, label = "Search", modifier = Modifier.testTag("action_search")) {
                    showSearchDialog = true
                }
                QuickActionButton(icon = Icons.Default.Memory, label = "Memory", modifier = Modifier.testTag("action_memory")) {
                    onNavigateMemory()
                }
                QuickActionButton(icon = Icons.Default.Folder, label = "Files", modifier = Modifier.testTag("action_files")) {
                    onNavigateFiles()
                }
                QuickActionButton(icon = Icons.Default.PhoneAndroid, label = "System", modifier = Modifier.testTag("action_system")) {
                    DeviceActionHandler.openSettings(context)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Text Input & Mic Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Ask JARVIS anything...", color = Color.Gray) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("jarvis_input"),
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JarvisCyan,
                        unfocusedBorderColor = JarvisCard,
                        focusedContainerColor = JarvisSurface,
                        unfocusedContainerColor = JarvisSurface,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                FloatingActionButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendMessage(textInput)
                            textInput = ""
                            onNavigateChat()
                        }
                    },
                    containerColor = JarvisCyan,
                    contentColor = JarvisNavy,
                    modifier = Modifier.size(52.dp).testTag("send_button")
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }

                Spacer(modifier = Modifier.width(8.dp))

                FloatingActionButton(
                    onClick = {
                        val permission = Manifest.permission.RECORD_AUDIO
                        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                            viewModel.sendMessage("Voice command received, Sir.")
                            onNavigateChat()
                        } else {
                            micPermissionLauncher.launch(permission)
                        }
                    },
                    containerColor = JarvisCard,
                    contentColor = JarvisCyan,
                    modifier = Modifier.size(52.dp).testTag("mic_button")
                ) {
                    Icon(Icons.Default.Mic, contentDescription = "Microphone")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Conversations Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("RECENT PROTOCOLS", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                TextButton(onClick = onNavigateHistory) {
                    Text("View All", color = JarvisCyan)
                }
            }

            // Recent Conversations List
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(conversations.take(3)) { conv ->
                    Card(
                        onClick = {
                            viewModel.setConversation(conv.id)
                            onNavigateChat()
                        },
                        colors = CardDefaults.cardColors(containerColor = JarvisSurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = JarvisCyan)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(conv.title, color = Color.White, fontWeight = FontWeight.Medium)
                                Text("Tap to resume protocol", color = Color.Gray, fontSize = 12.sp)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                        }
                    }
                }
            }

            if (showSearchDialog) {
                AlertDialog(
                    onDismissRequest = { showSearchDialog = false },
                    title = { Text("Web Search Grounding") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Enter query for JARVIS to search the web, Sir:", color = Color.Gray, fontSize = 13.sp)
                            OutlinedTextField(
                                value = searchQueryText,
                                onValueChange = { searchQueryText = it },
                                label = { Text("Search Query") },
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (searchQueryText.isNotBlank()) {
                                val query = searchQueryText
                                searchQueryText = ""
                                showSearchDialog = false
                                viewModel.sendMessage("Please search the web and summarize current info for: $query")
                                onNavigateChat()
                            }
                        }) {
                            Text("Search & Consult", color = JarvisCyan)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSearchDialog = false }) {
                            Text("Cancel")
                        }
                    },
                    containerColor = JarvisSurface
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(JarvisSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = JarvisCyan)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, color = Color.Gray, fontSize = 12.sp)
    }
}
