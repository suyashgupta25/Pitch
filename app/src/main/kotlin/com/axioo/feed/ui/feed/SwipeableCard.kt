package com.axioo.feed.ui.feed

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.dp
import com.axioo.feed.ui.theme.AxiooMotion
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

private enum class Axis { Horizontal, Vertical }

enum class SwipeOutcome { Like, Pass, Save }

/**
 * Wraps a card with axis-locked drag detection so horizontal swipes (like / pass) and the parent
 * VerticalPager's vertical swipes (next pitch) coexist cleanly.
 *
 */
@Composable
fun Modifier.swipeableCard(
    enabled: Boolean,
    onSwipe: (SwipeOutcome) -> Unit,
    onTap: () -> Unit,
): Modifier {
    val density = LocalDensity.current
    val viewConfig = LocalViewConfiguration.current
    val screenWidthPx =
        with(density) {
            LocalConfiguration.current.screenWidthDp.dp
                .toPx()
        }
    val touchSlop = viewConfig.touchSlop
    val flingThreshold = screenWidthPx * FLING_THRESHOLD_FRACTION

    val translationX = remember { Animatable(0f) }
    val rotationZ = remember { Animatable(0f) }
    var dispatchedOutcome by remember { mutableStateOf<SwipeOutcome?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(dispatchedOutcome) {
        val outcome = dispatchedOutcome
        if (outcome == SwipeOutcome.Like || outcome == SwipeOutcome.Pass) {
            onSwipe(outcome)
            translationX.snapTo(0f)
            rotationZ.snapTo(0f)
            dispatchedOutcome = null
        }
    }

    return this
        .graphicsLayer {
            this.translationX = translationX.value
            this.rotationZ = rotationZ.value
        }.pointerInput(enabled) {
            if (!enabled) return@pointerInput
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = true)
                var axis: Axis? = null
                var totalDx = 0f
                var totalDy = 0f
                var pointerId = down.id

                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Main)
                    val change = event.changes.firstOrNull { it.id == pointerId } ?: break
                    if (change.changedToUp()) {
                        if (axis == Axis.Horizontal) {
                            val current = translationX.value
                            scope.launch {
                                if (abs(current) >= flingThreshold) {
                                    val dir = sign(current)
                                    val target = dir * screenWidthPx * FLING_OFF_SCREEN_MULTIPLIER
                                    translationX.animateTo(target, tween(durationMillis = FLING_DURATION_MS))
                                    rotationZ.animateTo(dir * FLING_ROTATION_DEGREES, tween(FLING_DURATION_MS))
                                    dispatchedOutcome = if (dir > 0f) SwipeOutcome.Like else SwipeOutcome.Pass
                                } else {
                                    translationX.animateTo(0f, AxiooMotion.cardSpring)
                                    rotationZ.animateTo(0f, AxiooMotion.cardSpring)
                                }
                            }
                        }
                        break
                    }
                    val delta: Offset = change.positionChange()
                    totalDx += delta.x
                    totalDy += delta.y

                    if (axis == null && (abs(totalDx) > touchSlop || abs(totalDy) > touchSlop)) {
                        axis = if (abs(totalDx) > abs(totalDy)) Axis.Horizontal else Axis.Vertical
                    }
                    if (axis == Axis.Horizontal) {
                        change.consume()
                        scope.launch {
                            translationX.snapTo(translationX.value + delta.x)
                            rotationZ.snapTo((translationX.value / screenWidthPx) * MAX_TILT_DEGREES)
                        }
                    }
                    // axis == Vertical: do not consume — let parent pager handle it
                }
            }
        }.pointerInput(enabled) {
            if (!enabled) return@pointerInput
            detectTapGestures(onTap = { onTap() })
        }
}

private const val FLING_THRESHOLD_FRACTION = 0.30f
private const val FLING_OFF_SCREEN_MULTIPLIER = 1.4f
private const val FLING_DURATION_MS = 220
private const val FLING_ROTATION_DEGREES = 18f
private const val MAX_TILT_DEGREES = 14f
