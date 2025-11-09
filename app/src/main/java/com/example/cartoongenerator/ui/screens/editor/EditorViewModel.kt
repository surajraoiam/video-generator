package com.example.cartoongenerator.ui.screens.editor

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartoongenerator.camera.CameraManager
import com.example.cartoongenerator.video.StylePreset
import com.example.cartoongenerator.video.VideoProcessor
import com.example.cartoongenerator.data.repository.CartoonRepository
import android.graphics.Bitmap
import android.content.Intent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val cameraManager: CameraManager,
    private val videoProcessor: VideoProcessor,
    private val cartoonRepository: CartoonRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState

    private val _processingProgress = MutableStateFlow<Float?>(null)
    val processingProgress: StateFlow<Float?> = _processingProgress

    fun onStyleSelected(style: CartoonStyle) {
        _uiState.value = _uiState.value.copy(selectedStyle = style)
    }

    fun generateCartoon(inputVideoUri: Uri, outputFile: File) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isProcessing = true)
                
                val stylePreset = when (_uiState.value.selectedStyle) {
                    CartoonStyle.ANIME -> StylePreset.ANIME
                    CartoonStyle.COMIC -> StylePreset.COMIC
                    CartoonStyle.PIXAR -> StylePreset.PIXAR
                }

                val tempInputFile = File.createTempFile("input_video", ".mp4")
                // Copy input video to temp file
                inputVideoUri.toFile().copyTo(tempInputFile, overwrite = true)

                videoProcessor.processVideo(
                    inputVideoFile = tempInputFile,
                    outputVideoFile = outputFile,
                    stylePreset = stylePreset
                ) { progress ->
                    _processingProgress.value = progress
                }.collect { state ->
                    when (state) {
                        is ProcessingState.Completed -> {
                            _uiState.value = _uiState.value.copy(
                                processedVideoUri = Uri.fromFile(state.outputFile)
                            )
                        }
                        is ProcessingState.Error -> {
                            _uiState.value = _uiState.value.copy(error = state.message)
                        }
                        else -> {} // Handle other states if needed
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isProcessing = false)
                _processingProgress.value = null
            }
        }
    }

    fun captureVideo() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isRecording = true)
                // Implement video capture logic using CameraManager
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isRecording = false)
            }
        }
    }
    }

    // Save cartoon image (bitmap) with title
    fun saveCartoon(bitmap: Bitmap, title: String) {
        viewModelScope.launch {
            try {
                cartoonRepository.saveCartoonImage(bitmap, title)
                // Optionally update UI state to show success
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    // Share cartoon by file
    fun shareCartoon(file: java.io.File, onShareIntentReady: (Intent) -> Unit) {
        viewModelScope.launch {
            try {
                val intent = cartoonRepository.shareCartoonByFile(file)
                onShareIntentReady(intent)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}

data class EditorUiState(
    val selectedStyle: CartoonStyle = CartoonStyle.ANIME,
    val isProcessing: Boolean = false,
    val isRecording: Boolean = false,
    val processedVideoUri: Uri? = null,
    val error: String? = null
)

enum class CartoonStyle {
    ANIME,
    COMIC,
    PIXAR
}

private fun Uri.toFile(): File = File(this.path!!)