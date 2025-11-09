package com.example.cartoongenerator.ui.screens.editor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.camera.view.PreviewView
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cartoongenerator.camera.CameraManager

@Composable
fun CameraPreview(
    onVideoCapture: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var previewView: PreviewView? by remember { mutableStateOf(null) }

    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    this.layoutParams = android.widget.FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    previewView = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Camera controls
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            IconButton(
                onClick = onVideoCapture,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Record Video",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }

    // Start camera when preview is ready
    LaunchedEffect(previewView) {
        previewView?.let { preview ->
            try {
                val cameraManager = CameraManager(context)
                cameraManager.startCamera(lifecycleOwner, preview)
            } catch (e: Exception) {
                // Handle camera initialization error
            }
        }
    }
}