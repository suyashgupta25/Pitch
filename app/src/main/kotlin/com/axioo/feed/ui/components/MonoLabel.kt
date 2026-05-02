package com.axioo.feed.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.axioo.feed.ui.theme.AxiooTokens

@Composable
fun MonoLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = AxiooTokens.palette.cream,
    style: TextStyle = AxiooTokens.type.monoLabel,
    upper: Boolean = true,
) {
    Text(
        text = if (upper) text.uppercase() else text,
        modifier = modifier,
        color = color,
        style = style,
    )
}
