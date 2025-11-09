package com.example.cartoongenerator.ml

import android.graphics.Bitmap
import android.graphics.Rect
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FaceDetectionService @Inject constructor() {
    private val faceDetector: FaceDetector by lazy {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
        
        FaceDetection.getClient(options)
    }

    data class FaceInfo(
        val boundingBox: Rect,
        val landmarks: Map<FaceLandmark, android.graphics.PointF>,
        val emotions: Map<Emotion, Float>
    )

    enum class FaceLandmark {
        LEFT_EYE,
        RIGHT_EYE,
        NOSE_BASE,
        MOUTH_LEFT,
        MOUTH_RIGHT,
        MOUTH_BOTTOM
    }

    enum class Emotion {
        SMILING,
        LEFT_EYE_CLOSED,
        RIGHT_EYE_CLOSED
    }

    fun detectFaces(image: Bitmap): Flow<List<FaceInfo>> = flow {
        try {
            val faces = faceDetector.process(InputImage.fromBitmap(image, 0)).await()
            
            val faceInfoList = faces.map { face ->
                // Extract landmarks
                val landmarks = mutableMapOf<FaceLandmark, android.graphics.PointF>()
                face.getLandmark(FaceDetectorOptions.LANDMARK_MODE_ALL)?.let { landmark ->
                    landmarks[FaceLandmark.LEFT_EYE] = landmark.position
                }
                // Add other landmarks similarly...

                // Extract emotions
                val emotions = mapOf(
                    Emotion.SMILING to face.smilingProbability ?: 0f,
                    Emotion.LEFT_EYE_CLOSED to face.leftEyeOpenProbability ?: 0f,
                    Emotion.RIGHT_EYE_CLOSED to face.rightEyeOpenProbability ?: 0f
                )

                FaceInfo(
                    boundingBox = face.boundingBox,
                    landmarks = landmarks,
                    emotions = emotions
                )
            }

            emit(faceInfoList)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.Default)

    fun cleanup() {
        faceDetector.close()
    }
}