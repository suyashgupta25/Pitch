package com.axioo.feed.ui.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VolumeOff
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.axioo.feed.R
import com.axioo.feed.ui.components.MonoLabel
import com.axioo.feed.ui.components.formatCompact
import com.axioo.feed.ui.feed.player.VideoSurface
import com.axioo.feed.ui.theme.AxiooTokens

@Composable
fun PitchCard(
    pitch: PitchUi,
    player: Player?,
    isMuted: Boolean,
    isCurrent: Boolean,
    onLike: () -> Unit,
    onSave: () -> Unit,
    onPass: () -> Unit,
    onToggleMute: () -> Unit,
    showLikeBurst: Boolean,
    showBookmarkPulse: Boolean,
    modifier: Modifier = Modifier,
) {
    val palette = AxiooTokens.palette
    val spacing = AxiooTokens.spacing

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(palette.ink)
                .swipeableCard(
                    enabled = isCurrent,
                    onSwipe = { outcome ->
                        when (outcome) {
                            SwipeOutcome.Like -> onLike()
                            SwipeOutcome.Pass -> onPass()
                            SwipeOutcome.Save -> onSave()
                        }
                    },
                    onTap = { onSave() },
                ),
    ) {
        // Poster underneath the player so the surface never flashes pure black during prepare.
        AsyncImage(
            model =
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(pitch.pitch.thumbnailUrl)
                    .crossfade(true)
                    .build(),
            contentDescription = stringResource(R.string.cd_thumbnail),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        VideoSurface(
            player = player,
            modifier = Modifier.fillMaxSize(),
        )

        // Top scrim — protects status bar / mute icon legibility.
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(SCRIM_TOP_HEIGHT_DP.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(palette.overlayScrim, Color.Transparent),
                        ),
                    ),
        )

        // Bottom scrim — protects overlay text.
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(SCRIM_BOTTOM_HEIGHT_DP.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, palette.overlayScrim),
                        ),
                    ),
        )

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = spacing.gutter, vertical = spacing.s),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CategoryChip(text = pitch.pitch.category)
            MuteToggle(isMuted = isMuted, onClick = onToggleMute)
        }

        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(BOTTOM_TEXT_WIDTH_FRACTION)
                    .padding(start = spacing.gutter, end = spacing.s, bottom = spacing.xl),
        ) {
            MonoLabel(
                text = pitch.pitch.founderName,
                color = palette.cream.copy(alpha = 0.7f),
                upper = false,
                style = AxiooTokens.type.monoCategory,
            )
            Spacer(Modifier.height(spacing.xs))
            Text(
                text = pitch.pitch.startupName,
                style = MaterialTheme.typography.displayMedium,
                color = palette.cream,
            )
            Spacer(Modifier.height(spacing.s))
            Text(
                text = pitch.pitch.oneLinePitch,
                style = MaterialTheme.typography.bodyLarge,
                color = palette.cream.copy(alpha = 0.85f),
            )
        }

        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = spacing.l, bottom = spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.l),
        ) {
            RailAction(
                icon = if (pitch.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                count = pitch.displayLikes,
                tint = if (pitch.isLiked) palette.orange else palette.cream,
                onClick = onLike,
            )
            RailAction(
                icon = if (pitch.isSaved) Icons.Filled.Bookmark else Icons.Outlined.Bookmark,
                count = pitch.displaySaves,
                tint = if (pitch.isSaved) palette.purple else palette.cream,
                onClick = onSave,
            )
            RailAction(
                icon = Icons.Outlined.Visibility,
                count = pitch.pitch.counts.views,
                tint = palette.cream.copy(alpha = 0.7f),
                onClick = null,
            )
        }

        AnimatedVisibility(visible = showLikeBurst, enter = fadeIn(), exit = fadeOut()) {
            LikeBurst(visible = showLikeBurst)
        }
        AnimatedVisibility(visible = showBookmarkPulse, enter = fadeIn(), exit = fadeOut()) {
            BookmarkPulse(visible = showBookmarkPulse)
        }
    }
}

@Composable
private fun CategoryChip(text: String) {
    val palette = AxiooTokens.palette
    val spacing = AxiooTokens.spacing
    Box(
        modifier =
            Modifier
                .clip(
                    androidx.compose.foundation.shape
                        .RoundedCornerShape(spacing.pillCorner),
                ).background(palette.cream.copy(alpha = 0.16f))
                .padding(horizontal = spacing.m, vertical = spacing.xs),
    ) {
        MonoLabel(text = text, color = palette.cream, style = AxiooTokens.type.monoLabel)
    }
}

@Composable
private fun MuteToggle(
    isMuted: Boolean,
    onClick: () -> Unit,
) {
    val palette = AxiooTokens.palette
    val spacing = AxiooTokens.spacing
    Box(
        modifier =
            Modifier
                .size(MUTE_TOGGLE_SIZE_DP.dp)
                .clip(CircleShape)
                .background(palette.ink.copy(alpha = 0.55f))
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (isMuted) Icons.Outlined.VolumeOff else Icons.Outlined.VolumeUp,
            contentDescription =
                stringResource(
                    if (isMuted) R.string.feed_mute_on else R.string.feed_mute_off,
                ),
            tint = palette.cream,
            modifier = Modifier.size(MUTE_ICON_SIZE_DP.dp),
        )
    }
}

@Composable
private fun RailAction(
    icon: ImageVector,
    count: Long,
    tint: Color,
    onClick: (() -> Unit)?,
) {
    val spacing = AxiooTokens.spacing
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            if (onClick != null) {
                Modifier.clickable(onClick = onClick).padding(spacing.xxs)
            } else {
                Modifier.padding(spacing.xxs)
            },
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(RAIL_ICON_SIZE_DP.dp),
        )
        Spacer(Modifier.height(spacing.xs))
        Text(
            text = count.formatCompact(),
            color = tint,
            style = AxiooTokens.type.monoCount,
        )
    }
}

private const val SCRIM_TOP_HEIGHT_DP = 120
private const val SCRIM_BOTTOM_HEIGHT_DP = 280
private const val BOTTOM_TEXT_WIDTH_FRACTION = 0.78f
private const val MUTE_TOGGLE_SIZE_DP = 36
private const val MUTE_ICON_SIZE_DP = 18
private const val RAIL_ICON_SIZE_DP = 28
