package com.example.cartoongenerator.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import android.graphics.drawable.Drawable

@Composable
fun AnimatedCharacter(
    character: Drawable,
    modifier: Modifier = Modifier,
    onViewCreated: (AnimatedCharacterView) -> Unit = {}
) {
    val context = LocalContext.current
    
    val view = remember {
        AnimatedCharacterView(context).apply {
            setCharacterDrawable(character)
        }
    }
    
    DisposableEffect(view) {
        onDispose {
            // Cleanup if needed
        }
    }
    
    AndroidView(
        factory = { view },
        modifier = modifier,
        update = { animatedView ->
            onViewCreated(animatedView)
        }
    )
}