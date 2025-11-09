package com.example.cartoongenerator.ui.screens.editor

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditorViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Cartoon") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Save cartoon */ }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Camera Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .weight(1f)
            ) {
                CameraPreview(
                    onVideoCapture = viewModel::captureVideo
                )
                
                // Processing indicator
                if (viewModel.uiState.value.isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                    viewModel.processingProgress.value?.let { progress ->
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                        )
                    }
                }
            }

            // Style Controls
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Cartoon Style",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FilterChip(
                            selected = true,
                            onClick = { },
                            label = { Text("Anime") }
                        )
                        FilterChip(
                            selected = false,
                            onClick = { },
                            label = { Text("Comic") }
                        )
                        FilterChip(
                            selected = false,
                            onClick = { },
                            label = { Text("Pixar") }
                        )
                    }
                }
            }

            Button(
                onClick = { /* Generate cartoon */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generate Cartoon")
            }

            // Save and Share buttons (visible if cartoon is generated)
            if (viewModel.uiState.value.processedVideoUri != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        // Save cartoon (example: pass bitmap and title)
                        // viewModel.saveCartoon(bitmap, "My Cartoon")
                    }) {
                        Text("Save")
                    }
                    Button(onClick = {
                        // Share cartoon (example: pass file and handle intent)
                        // val file = File(viewModel.uiState.value.processedVideoUri!!.path!!)
                        // viewModel.shareCartoon(file) { intent -> /* launch share intent */ }
                    }) {
                        Text("Share")
                    }
                }
            }
        }
    }
}