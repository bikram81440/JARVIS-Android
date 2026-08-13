package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisNavy
import com.example.ui.theme.JarvisSurface
import com.example.viewmodel.JarvisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: JarvisViewModel,
    onNavigateAbout: () -> Unit,
    onNavigatePrivacy: () -> Unit,
    onBack: () -> Unit
) {
    val voiceEnabled by viewModel.voiceEnabled.collectAsState()
    val speechSpeed by viewModel.speechSpeed.collectAsState()
    val autoSpeak by viewModel.autoSpeak.collectAsState()
    val webSearchEnabled by viewModel.webSearchEnabled.collectAsState()
    val memoryEnabled by viewModel.memoryEnabled.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Configuration", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = JarvisNavy)
            )
        },
        containerColor = JarvisNavy
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("AI NEURAL ENGINE", color = JarvisCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = JarvisSurface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Primary Neural Model", color = Color.White)
                            Text(selectedModel, color = JarvisCyan, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Text("VOICE SYNTHESIS", color = JarvisCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = JarvisSurface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Voice Feedback Active", color = Color.White)
                            Switch(
                                checked = voiceEnabled,
                                onCheckedChange = { viewModel.voiceEnabled.value = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = JarvisCyan)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Auto-Speak Responses", color = Color.White)
                            Switch(
                                checked = autoSpeak,
                                onCheckedChange = { viewModel.autoSpeak.value = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = JarvisCyan)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Speech Cadence: ${String.format("%.1fx", speechSpeed)}", color = Color.White)
                        Slider(
                            value = speechSpeed,
                            onValueChange = { viewModel.speechSpeed.value = it },
                            valueRange = 0.5f..2.0f,
                            colors = SliderDefaults.colors(thumbColor = JarvisCyan, activeTrackColor = JarvisCyan)
                        )
                    }
                }
            }

            item {
                Text("INTELLIGENCE PROTOCOLS", color = JarvisCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = JarvisSurface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Live Web Search Grounding", color = Color.White)
                            Switch(
                                checked = webSearchEnabled,
                                onCheckedChange = { viewModel.webSearchEnabled.value = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = JarvisCyan)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Persistent Neural Memory", color = Color.White)
                            Switch(
                                checked = memoryEnabled,
                                onCheckedChange = { viewModel.memoryEnabled.value = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = JarvisCyan)
                            )
                        }
                    }
                }
            }

            item {
                Text("INFORMATION & PRIVACY", color = JarvisCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = JarvisSurface)) {
                    Column {
                        ListItem(
                            headlineContent = { Text("About J.A.R.V.I.S.", color = Color.White) },
                            trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray) },
                            modifier = Modifier.clickable { onNavigateAbout() },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                        Divider(color = JarvisNavy)
                        ListItem(
                            headlineContent = { Text("Privacy Policy & Security", color = Color.White) },
                            trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray) },
                            modifier = Modifier.clickable { onNavigatePrivacy() },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}
