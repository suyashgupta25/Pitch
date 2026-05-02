package com.axioo.feed.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.axioo.feed.ui.theme.AxiooTokens

@Composable
fun EmptyState(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    accentLabel: String? = null,
) {
    val palette = AxiooTokens.palette
    val spacing = AxiooTokens.spacing

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(spacing.xxxl)
                    .background(palette.purple, RoundedCornerShape(spacing.pillCorner)),
        )
        if (accentLabel != null) {
            MonoLabel(
                text = accentLabel,
                color = palette.purple,
                modifier = Modifier.padding(top = spacing.l),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = spacing.l),
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = spacing.s, start = spacing.xl, end = spacing.xl),
        )
    }
}
