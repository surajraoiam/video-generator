package com.example.cartoongenerator.ml

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.style.StyleTransferModel
import com.google.mlkit.vision.style.StylesModelIdentifier
import com.google.mlkit.vision.style.StyleTransfer
import com.google.mlkit.vision.style.StyleTransferProcessor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StyleTransferService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var styleTransferProcessor: StyleTransferProcessor? = null
    private var currentStyle: CartoonStyle = CartoonStyle.ANIME

    sealed class StyleTransferResult {
        data class Success(val processedBitmap: Bitmap) : StyleTransferResult()
        data class Error(val message: String) : StyleTransferResult()
        object Processing : StyleTransferResult()
    }

    fun processImage(
        inputImage: Bitmap,
        style: CartoonStyle
    ): Flow<StyleTransferResult> = flow {
        try {
            emit(StyleTransferResult.Processing)

            // Initialize or update style transfer processor if style changed
            if (styleTransferProcessor == null || currentStyle != style) {
                styleTransferProcessor?.close()
                styleTransferProcessor = createStyleTransferProcessor(style)
                currentStyle = style
            }

            val processor = styleTransferProcessor ?: throw IllegalStateException("Failed to initialize style transfer")
            val mlKitImage = InputImage.fromBitmap(inputImage, 0)
            
            // Process the image
            val styledBitmap = processor.process(mlKitImage).await()
            emit(StyleTransferResult.Success(styledBitmap))

        } catch (e: Exception) {
            emit(StyleTransferResult.Error(e.message ?: "Unknown error occurred"))
        }
    }.flowOn(Dispatchers.Default)

    private suspend fun createStyleTransferProcessor(style: CartoonStyle): StyleTransferProcessor {
        val modelIdentifier = when (style) {
            CartoonStyle.ANIME -> StylesModelIdentifier.ANIME
            CartoonStyle.COMIC -> StylesModelIdentifier.ASHBY
            CartoonStyle.PIXAR -> StylesModelIdentifier.CUTE
        }

        val model = StyleTransferModel.createFromModelIdentifier(modelIdentifier).await()
        return StyleTransferProcessor.createFromModel(model)
    }

    fun processBatch(
        images: List<Bitmap>,
        style: CartoonStyle
    ): Flow<List<StyleTransferResult>> = flow {
        val results = mutableListOf<StyleTransferResult>()
        
        try {
            // Initialize processor
            if (styleTransferProcessor == null || currentStyle != style) {
                styleTransferProcessor?.close()
                styleTransferProcessor = createStyleTransferProcessor(style)
                currentStyle = style
            }

            val processor = styleTransferProcessor ?: throw IllegalStateException("Failed to initialize style transfer")

            // Process each image
            for (image in images) {
                val mlKitImage = InputImage.fromBitmap(image, 0)
                try {
                    val styledBitmap = processor.process(mlKitImage).await()
                    results.add(StyleTransferResult.Success(styledBitmap))
                } catch (e: Exception) {
                    results.add(StyleTransferResult.Error(e.message ?: "Failed to process image"))
                }
                emit(results.toList())
            }

        } catch (e: Exception) {
            emit(results + StyleTransferResult.Error(e.message ?: "Unknown error occurred"))
        }
    }.flowOn(Dispatchers.Default)

    fun cleanup() {
        styleTransferProcessor?.close()
        styleTransferProcessor = null
    }
}

enum class CartoonStyle {
    ANIME,
    COMIC,
    PIXAR
}