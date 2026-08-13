package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun FilesScreen(
    viewModel: JarvisViewModel,
    onBack: () -> Unit
) {
    var selectedFiles by remember { mutableStateOf(listOf<Uri>()) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && !selectedFiles.contains(uri)) {
            selectedFiles = selectedFiles + uri
            viewModel.sendMessage("Added document for analysis: ${uri.lastPathSegment}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Secure Document Vault", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("files_back")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = JarvisNavy)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { filePickerLauncher.launch("*/*") },
                containerColor = JarvisCyan,
                contentColor = JarvisNavy,
                modifier = Modifier.testTag("pick_file_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Select File")
            }
        },
        containerColor = JarvisNavy
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Select local documents and files securely via system picker for AI intelligence ingestion.",
                color = Color.Gray,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedFiles.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No documents loaded in vault, Sir.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(selectedFiles) { uri ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = JarvisSurface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = JarvisCyan)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(uri.lastPathSegment ?: "Document", color = Color.White, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(uri.toString(), color = Color.Gray, fontSize = 11.sp, maxLines = 1)
                                }
                                IconButton(onClick = { selectedFiles = selectedFiles - uri }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
