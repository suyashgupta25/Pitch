package com.axioo.feed.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

enum class AxiooSurfaceMode { Dark, Light }

val LocalAxiooPalette = staticCompositionLocalOf { AxiooPaletteValues }
val LocalAxiooTypeTokens = staticCompositionLocalOf<AxiooTypeTokens?> { null }

@Composable
fun AxiooTheme(
    mode: AxiooSurfaceMode = AxiooSurfaceMode.Dark,
    content: @Composable () -> Unit,
) {
    val palette = AxiooPaletteValues
    val typeTokens = rememberAxiooTypeTokens()
    val colorScheme =
        when (mode) {
            AxiooSurfaceMode.Dark -> darkColors(palette)
            AxiooSurfaceMode.Light -> lightColors(palette)
        }

    CompositionLocalProvider(
        LocalAxiooPalette provides palette,
        LocalAxiooTypeTokens provides typeTokens,
        LocalAxiooSpacing provides AxiooSpacing(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typeTokens.typography,
            shapes = AxiooShapes,
            content = content,
        )
    }
}

object AxiooTokens {
    val palette: AxiooPalette
        @Composable get() = LocalAxiooPalette.current

    val type: AxiooTypeTokens
        @Composable get() =
            checkNotNull(LocalAxiooTypeTokens.current) {
                "AxiooTheme not applied — wrap your composable in AxiooTheme { ... }"
            }

    val spacing: AxiooSpacing
        @Composable get() = LocalAxiooSpacing.current
}
