package com.axioo.feed.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** 4dp grid. All spacing is a multiple of 4. */
@Immutable
data class AxiooSpacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val s: Dp = 8.dp,
    val m: Dp = 12.dp,
    val l: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 48.dp,
    val gutter: Dp = 20.dp,
    val cardCorner: Dp = 0.dp,
    val pillCorner: Dp = 28.dp,
    val hairline: Dp = 1.dp,
)

val LocalAxiooSpacing = staticCompositionLocalOf { AxiooSpacing() }
