package com.example.cartoongenerator.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimationManager @Inject constructor() {
    fun createBounceAnimation(view: View, duration: Long = 300): AnimatorSet {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.2f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.2f, 1f)
        
        return AnimatorSet().apply {
            playTogether(ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY).apply {
                this.duration = duration
            })
        }
    }

    fun createWalkAnimation(view: View, distance: Float, duration: Long = 1000): AnimatorSet {
        val translation = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0f, distance)
        val bounce = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, -20f, 0f)
        
        return AnimatorSet().apply {
            playTogether(translation, bounce)
            this.duration = duration
        }
    }

    fun createJumpAnimation(view: View, height: Float = 100f, duration: Long = 500): AnimatorSet {
        val jump = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, -height, 0f)
        val squash = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.2f, 1f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0.8f, 1f)
        )

        return AnimatorSet().apply {
            playSequentially(squash, jump)
            this.duration = duration
        }
    }

    fun createSpinAnimation(view: View, duration: Long = 1000): AnimatorSet {
        val rotation = ObjectAnimator.ofFloat(view, View.ROTATION, 0f, 360f)
        
        return AnimatorSet().apply {
            play(rotation)
            this.duration = duration
        }
    }

    fun createEmotionAnimation(view: View, emotion: Emotion, duration: Long = 500): AnimatorSet {
        return when (emotion) {
            Emotion.HAPPY -> createBounceAnimation(view, duration)
            Emotion.SAD -> createDropAnimation(view, duration)
            Emotion.EXCITED -> createShakeAnimation(view, duration)
            Emotion.SURPRISED -> createPopAnimation(view, duration)
        }
    }

    private fun createDropAnimation(view: View, duration: Long): AnimatorSet {
        val drop = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, 20f)
        val squish = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.2f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0.8f)
        )

        return AnimatorSet().apply {
            playTogether(drop, squish)
            this.duration = duration
        }
    }

    private fun createShakeAnimation(view: View, duration: Long): AnimatorSet {
        val shake = ObjectAnimator.ofFloat(view, View.ROTATION, 0f, 5f, -5f, 5f, -5f, 0f)
        
        return AnimatorSet().apply {
            play(shake)
            this.duration = duration
        }
    }

    private fun createPopAnimation(view: View, duration: Long): AnimatorSet {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.4f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.4f, 1f)
        
        return AnimatorSet().apply {
            playTogether(ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY).apply {
                this.duration = duration
            })
        }
    }
}

enum class Emotion {
    HAPPY,
    SAD,
    EXCITED,
    SURPRISED
}