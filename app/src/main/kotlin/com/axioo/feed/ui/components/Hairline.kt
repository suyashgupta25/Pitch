package com.axioo.feed.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.axioo.feed.ui.theme.AxiooTokens

@Composable
fun Hairline(
    modifier: Modifier = Modifier,
    color: Color = AxiooTokens.palette.hairline,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(AxiooTokens.spacing.hairline)
                .background(color),
    )
}
