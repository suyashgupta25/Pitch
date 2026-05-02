package com.axioo.feed.ui.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.axioo.feed.R
import com.axioo.feed.domain.model.Pitch
import com.axioo.feed.domain.model.PitchId
import com.axioo.feed.ui.components.EmptyState
import com.axioo.feed.ui.components.MonoLabel
import com.axioo.feed.ui.theme.AxiooTokens

@Composable
fun BookmarksScreen(
    onOpenFocused: (PitchId) -> Unit,
    viewModel: BookmarksViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val palette = AxiooTokens.palette
    val spacing = AxiooTokens.spacing

    Box(modifier = Modifier.fillMaxSize().background(palette.cream)) {
        when (val s = state) {
            BookmarksUiState.Loading -> Spacer(Modifier.fillMaxSize())
            BookmarksUiState.Empty ->
                EmptyState(
                    title = stringResource(R.string.bookmarks_empty_title),
                    body = stringResource(R.string.bookmarks_empty_body),
                    accentLabel = stringResource(R.string.bookmarks_title),
                )
            is BookmarksUiState.Ready ->
                Column(modifier = Modifier.fillMaxSize()) {
                    Header(title = stringResource(R.string.bookmarks_title), count = s.items.size)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(BOOKMARKS_COLUMN_COUNT),
                        contentPadding =
                            androidx.compose.foundation.layout.PaddingValues(
                                horizontal = spacing.gutter,
                                vertical = spacing.l,
                            ),
                        verticalArrangement = Arrangement.spacedBy(spacing.l),
                        horizontalArrangement = Arrangement.spacedBy(spacing.l),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(items = s.items, key = { it.id.value }) { pitch ->
                            BookmarkCell(pitch = pitch, onClick = { onOpenFocused(pitch.id) })
                        }
                    }
                }
        }
    }
}

@Composable
private fun Header(
    title: String,
    count: Int,
) {
    val palette = AxiooTokens.palette
    val spacing = AxiooTokens.spacing
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = spacing.gutter, vertical = spacing.l),
    ) {
        MonoLabel(
            text = "$count saved",
            color = palette.purple,
            style = AxiooTokens.type.monoCategory,
        )
        Spacer(Modifier.height(spacing.xs))
        Text(
            text = title,
            color = palette.ink,
            style = MaterialTheme.typography.displayMedium,
        )
    }
}

@Composable
private fun BookmarkCell(
    pitch: Pitch,
    onClick: () -> Unit,
) {
    val palette = AxiooTokens.palette
    val spacing = AxiooTokens.spacing
    Column(modifier = Modifier.clickable(onClick = onClick)) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(BOOKMARK_TILE_ASPECT)
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
        MonoLabel(
            text = pitch.category,
            color = palette.purple,
            style = AxiooTokens.type.monoCategory,
        )
        Spacer(Modifier.height(spacing.xxs))
        Text(
            text = pitch.startupName,
            color = palette.ink,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

private const val BOOKMARKS_COLUMN_COUNT = 2
private const val BOOKMARK_TILE_ASPECT = 0.7f
