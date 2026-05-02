package com.axioo.feed.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.axioo.feed.R
import com.axioo.feed.domain.model.Pitch
import com.axioo.feed.domain.model.PitchId
import com.axioo.feed.ui.components.MonoLabel
import com.axioo.feed.ui.theme.AxiooTokens
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ActivityCarousel(
    title: String,
    items: ImmutableList<Pitch>,
    onClick: (PitchId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AxiooTokens.palette
    val spacing = AxiooTokens.spacing

    Column(modifier = modifier.fillMaxWidth()) {
        Row(title = title)
        Spacer(Modifier.height(spacing.s))
        if (items.isEmpty()) {
            Text(
                text = stringResource(R.string.profile_empty_carousel),
                color = palette.ink.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = spacing.gutter, vertical = spacing.m),
            )
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = spacing.gutter),
                horizontalArrangement = Arrangement.spacedBy(spacing.m),
            ) {
                items(items = items, key = { it.id.value }) { pitch ->
                    Tile(pitch = pitch, onClick = { onClick(pitch.id) })
                }
            }
        }
    }
}

@Composable
private fun Row(title: String) {
    val palette = AxiooTokens.palette
    val spacing = AxiooTokens.spacing
    androidx.compose.foundation.layout.Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.gutter, vertical = spacing.s),
    ) {
        MonoLabel(
            text = title,
            color = palette.purple,
            style = AxiooTokens.type.monoLabel,
        )
    }
}

@Composable
private fun Tile(
    pitch: Pitch,
    onClick: () -> Unit,
) {
    val palette = AxiooTokens.palette
    val spacing = AxiooTokens.spacing
    Column(
        modifier =
            Modifier
                .width(TILE_WIDTH_DP.dp)
                .clickable(onClick = onClick),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(TILE_ASPECT)
                    .background(palette.creamSoft),
        ) {
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(pitch.thumbnailUrl)
                        .crossfade(true)
                        .build(),
                contentDescription = stringResource(R.string.cd_thumbnail),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
        Spacer(Modifier.height(spacing.s))
        Text(
            text = pitch.startupName,
            color = palette.ink,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
        )
        Spacer(Modifier.height(spacing.xxs))
        MonoLabel(
            text = pitch.category,
            color = palette.ink.copy(alpha = 0.6f),
            style = AxiooTokens.type.monoCategory,
        )
    }
}

private const val TILE_WIDTH_DP = 140
private const val TILE_ASPECT = 0.7f
