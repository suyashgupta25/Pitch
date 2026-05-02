package com.axioo.feed.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class AxiooPalette(
    val orange: Color,
    val purple: Color,
    val cream: Color,
    val ink: Color,
    val inkSoft: Color,
    val creamSoft: Color,
    val onInk: Color,
    val onCream: Color,
    val hairline: Color,
    val overlayScrim: Color,
)

val AxiooPaletteValues =
    AxiooPalette(
        orange = Color(0xFFFE572A),
        purple = Color(0xFF685BC7),
        cream = Color(0xFFF3F0EB),
        ink = Color(0xFF201E1F),
        inkSoft = Color(0xFF2A2829),
        creamSoft = Color(0xFFE8E4DD),
        onInk = Color(0xFFF3F0EB),
        onCream = Color(0xFF201E1F),
        hairline = Color(0x33F3F0EB),
        overlayScrim = Color(0x66000000),
    )

internal fun darkColors(p: AxiooPalette): ColorScheme =
    darkColorScheme(
        primary = p.orange,
        onPrimary = p.ink,
        secondary = p.purple,
        onSecondary = p.cream,
        background = p.ink,
        onBackground = p.cream,
        surface = p.ink,
        onSurface = p.cream,
        surfaceVariant = p.inkSoft,
        onSurfaceVariant = p.cream,
        outline = p.hairline,
    )

internal fun lightColors(p: AxiooPalette): ColorScheme =
    lightColorScheme(
        primary = p.orange,
        onPrimary = p.cream,
        secondary = p.purple,
        onSecondary = p.cream,
        background = p.cream,
        onBackground = p.ink,
        surface = p.cream,
        onSurface = p.ink,
        surfaceVariant = p.creamSoft,
        onSurfaceVariant = p.ink,
        outline = Color(0x33201E1F),
    )
