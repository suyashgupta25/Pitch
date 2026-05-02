package com.axioo.feed.ui.feed

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.axioo.feed.ui.theme.AxiooTokens

@Composable
fun LikeBurst(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    Burst(visible = visible, modifier = modifier) { scale, alpha ->
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = null,
            tint = AxiooTokens.palette.orange,
            modifier =
                Modifier
                    .size(BURST_SIZE_DP.dp)
                    .scale(scale)
                    .graphicsLayer { this.alpha = alpha },
        )
    }
}

@Composable
fun BookmarkPulse(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    Burst(visible = visible, modifier = modifier) { scale, alpha ->
        Icon(
            imageVector = Icons.Filled.Bookmark,
            contentDescription = null,
            tint = AxiooTokens.palette.purple,
            modifier =
                Modifier
                    .size(BURST_SIZE_DP.dp)
                    .scale(scale)
                    .graphicsLayer { this.alpha = alpha },
        )
    }
}

@Composable
private fun Burst(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (scale: Float, alpha: Float) -> Unit,
) {
    val scale = remember { Animatable(BURST_INITIAL_SCALE) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(visible) {
        if (visible) {
            scale.snapTo(BURST_INITIAL_SCALE)
            alpha.snapTo(1f)
            scale.animateTo(BURST_PEAK_SCALE, tween(durationMillis = BURST_PEAK_MS))
            alpha.animateTo(0f, tween(durationMillis = BURST_FADE_MS))
        }
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        content(scale.value, alpha.value)
    }
}

private const val BURST_SIZE_DP = 96
private const val BURST_INITIAL_SCALE = 0.6f
private const val BURST_PEAK_SCALE = 1.4f
private const val BURST_PEAK_MS = 180
private const val BURST_FADE_MS = 220
