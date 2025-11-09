package com.example.cartoongenerator.animation

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale

class AnimatedCharacterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private var characterBitmap: Bitmap? = null
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var matrix = Matrix()
    
    private var scaleX = 1f
    private var scaleY = 1f
    private var rotation = 0f
    private var translationX = 0f
    private var translationY = 0f

    fun setCharacterDrawable(drawable: android.graphics.drawable.Drawable) {
        characterBitmap = drawable.toBitmap().scale(
            width = drawable.intrinsicWidth,
            height = drawable.intrinsicHeight,
            filter = true
        )
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        characterBitmap?.let { bitmap ->
            matrix.reset()
            
            // Apply transformations
            val px = width / 2f
            val py = height / 2f
            
            matrix.postTranslate(-bitmap.width / 2f, -bitmap.height / 2f)
            matrix.postScale(scaleX, scaleY)
            matrix.postRotate(rotation)
            matrix.postTranslate(px + translationX, py + translationY)
            
            canvas.drawBitmap(bitmap, matrix, paint)
        }
    }

    fun setScale(sx: Float, sy: Float) {
        scaleX = sx
        scaleY = sy
        invalidate()
    }

    fun setRotation(angle: Float) {
        rotation = angle
        invalidate()
    }

    fun setTranslation(tx: Float, ty: Float) {
        translationX = tx
        translationY = ty
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        characterBitmap?.recycle()
        characterBitmap = null
    }
}