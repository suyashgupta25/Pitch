package com.axioo.feed.ui.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.VolumeOff
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.axioo.feed.R
import com.axioo.feed.ui.components.MonoLabel
import com.axioo.feed.ui.feed.player.PitchWindow
import com.axioo.feed.ui.feed.player.VideoSurface
import com.axioo.feed.ui.feed.player.rememberPlayerPool
import com.axioo.feed.ui.theme.AxiooTokens

@Composable
fun FocusedPitchScreen(
    pitchId: String,
    onBack: () -> Unit,
    viewModel: FocusedPitchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val palette = AxiooTokens.palette
    val spacing = AxiooTokens.spacing

    Box(modifier = Modifier.fillMaxSize().background(palette.ink)) {
        when (val s = state) {
            FocusedPitchUiState.Loading -> Spacer(Modifier.fillMaxSize())
            is FocusedPitchUiState.NotFound ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Pitch not found",
                        color = palette.cream,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            is FocusedPitchUiState.Ready -> {
                val pool = rememberPlayerPool(capacity = 1)
                LaunchedEffect(s.isMuted) { pool.setMuted(s.isMuted) }
                val window =
                    remember(s.pitch.id) {
                        PitchWindow(
                            previous = null,
                            current = s.pitch.id to s.pitch.videoUrl,
                            next = null,
                        )
                    }
                val playersById = remember(window) { pool.bind(window) }

                AsyncImage(
                    model =
                        ImageRequest
                            .Builder(LocalContext.current)
                            .data(s.pitch.thumbnailUrl)
                            .crossfade(true)
                            .build(),
                    contentDescription = stringResource(R.string.cd_thumbnail),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                VideoSurface(
                    player = playersById[s.pitch.id],
                    modifier = Modifier.fillMaxSize(),
                )

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(spacing.l),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircleIcon(
                        icon = Icons.Outlined.ArrowBack,
                        onClick = onBack,
                        label = stringResource(R.string.cd_back),
                    )
                    Spacer(Modifier.fillMaxWidth(0f))
                }
                CircleIcon(
                    icon = if (s.isMuted) Icons.Outlined.VolumeOff else Icons.Outlined.VolumeUp,
                    onClick = viewModel::toggleMute,
                    label =
                        stringResource(
                            if (s.isMuted) R.string.feed_mute_on else R.string.feed_mute_off,
                        ),
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .statusBarsPadding()
                            .padding(spacing.l),
                )

                Column(
                    modifier =
                        Modifier
                            .align(Alignment.BottomStart)
                            .padding(spacing.gutter),
                ) {
                    MonoLabel(
                        text = s.pitch.category,
                        color = palette.cream.copy(alpha = 0.8f),
                        style = AxiooTokens.type.monoCategory,
                    )
                    Spacer(Modifier.height(spacing.xs))
                    Text(
                        text = s.pitch.startupName,
                        style = MaterialTheme.typography.displayMedium,
                        color = palette.cream,
                    )
                    Spacer(Modifier.height(spacing.s))
                    Text(
                        text = s.pitch.oneLinePitch,
                        style = MaterialTheme.typography.bodyLarge,
                        color = palette.cream.copy(alpha = 0.85f),
                    )
                }
            }
        }
    }
}

@Composable
private fun CircleIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    val palette = AxiooTokens.palette
    Box(
        modifier =
            modifier
                .size(BACK_ICON_SIZE_DP.dp)
                .clip(CircleShape)
                .background(palette.ink.copy(alpha = 0.55f))
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = palette.cream,
            modifier = Modifier.size(BACK_ICON_GLYPH_DP.dp),
        )
    }
}

private const val BACK_ICON_SIZE_DP = 40
private const val BACK_ICON_GLYPH_DP = 22
