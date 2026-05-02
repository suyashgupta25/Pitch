package com.axioo.feed.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.axioo.feed.R
import com.axioo.feed.ui.theme.AxiooTokens

enum class AxiooTab { Feed, Bookmarks, Profile }

@Composable
fun AxiooBottomBar(
    selected: AxiooTab,
    onSelect: (AxiooTab) -> Unit,
    modifier: Modifier = Modifier,
    onDarkSurface: Boolean = true,
) {
    val palette = AxiooTokens.palette
    val spacing = AxiooTokens.spacing
    val container = if (onDarkSurface) palette.ink else palette.cream
    val onContainer = if (onDarkSurface) palette.cream else palette.ink

    Column(modifier = modifier.fillMaxWidth().background(container)) {
        Hairline()
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BarItem(
                label = stringResource(R.string.nav_feed),
                icon = Icons.Outlined.PlayArrow,
                selected = selected == AxiooTab.Feed,
                onClick = { onSelect(AxiooTab.Feed) },
                onContainer = onContainer,
                accent = palette.orange,
            )
            BarItem(
                label = stringResource(R.string.nav_bookmarks),
                icon = Icons.Outlined.Bookmark,
                selected = selected == AxiooTab.Bookmarks,
                onClick = { onSelect(AxiooTab.Bookmarks) },
                onContainer = onContainer,
                accent = palette.orange,
            )
            BarItem(
                label = stringResource(R.string.nav_profile),
                icon = Icons.Outlined.Person,
                selected = selected == AxiooTab.Profile,
                onClick = { onSelect(AxiooTab.Profile) },
                onContainer = onContainer,
                accent = palette.orange,
            )
        }
    }
}

@Composable
private fun BarItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    onContainer: Color,
    accent: Color,
) {
    val spacing = AxiooTokens.spacing
    val color = if (selected) accent else onContainer.copy(alpha = 0.6f)
    Column(
        modifier =
            Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = spacing.l, vertical = spacing.s),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = color)
        Text(
            text = label.uppercase(),
            color = color,
            style = AxiooTokens.type.monoLabel,
            modifier = Modifier.padding(top = spacing.xxs),
        )
    }
}

@Composable
private fun stringResource(id: Int): String =
    androidx.compose.ui.res
        .stringResource(id)
