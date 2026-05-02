package com.axioo.feed.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axioo.feed.R
import com.axioo.feed.domain.model.PitchId
import com.axioo.feed.ui.feed.player.PitchWindow
import com.axioo.feed.ui.feed.player.rememberPlayerPool
import com.axioo.feed.ui.theme.AxiooTokens
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

const val FeedScreenTag = "axioo.feed.screen"
const val PitchCardTagPrefix = "axioo.feed.card."

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val palette = AxiooTokens.palette

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(palette.ink)
                .testTag(FeedScreenTag),
    ) {
        when (val s = state) {
            FeedUiState.Loading -> CenterText(stringResource(R.string.feed_loading))
            is FeedUiState.Error -> ErrorState(s.message, onRetry = { viewModel.onIntent(FeedIntent.Retry) })
            is FeedUiState.Ready -> ReadyContent(state = s, onIntent = viewModel::onIntent)
        }
    }
}

@Composable
private fun ReadyContent(
    state: FeedUiState.Ready,
    onIntent: (FeedIntent) -> Unit,
) {
    if (state.pitches.isEmpty()) {
        CenterText(stringResource(R.string.feed_loading))
        return
    }

    val pagerState = rememberPagerState(initialPage = state.currentIndex) { state.pitches.size }
    val pool = rememberPlayerPool()

    LaunchedEffect(state.isMuted) { pool.setMuted(state.isMuted) }

    LaunchedEffect(pagerState, state.pitches) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { onIntent(FeedIntent.PageChanged(it)) }
    }

    val current = state.currentIndex.coerceIn(0, state.pitches.size - 1)
    val window =
        remember(state.pitches, current) {
            PitchWindow(
                previous = state.pitches.getOrNull(current - 1)?.let { it.id to it.pitch.videoUrl },
                current = state.pitches.getOrNull(current)?.let { it.id to it.pitch.videoUrl },
                next = state.pitches.getOrNull(current + 1)?.let { it.id to it.pitch.videoUrl },
            )
        }
    val playersById = remember(window) { pool.bind(window) }

    var likeBurstFor by remember { mutableStateOf<PitchId?>(null) }
    var saveBurstFor by remember { mutableStateOf<PitchId?>(null) }

    LaunchedEffect(likeBurstFor) {
        if (likeBurstFor != null) {
            delay(BURST_DURATION_MS)
            likeBurstFor = null
        }
    }
    LaunchedEffect(saveBurstFor) {
        if (saveBurstFor != null) {
            delay(BURST_DURATION_MS)
            saveBurstFor = null
        }
    }

    VerticalPager(
        state = pagerState,
        pageSize = PageSize.Fill,
        beyondViewportPageCount = 1,
        modifier = Modifier.fillMaxSize(),
    ) { pageIndex ->
        val pitch = state.pitches[pageIndex]
        val isCurrent = pageIndex == current
        PitchCard(
            modifier = Modifier.testTag(PitchCardTagPrefix + pitch.id.value),
            pitch = pitch,
            player = playersById[pitch.id],
            isMuted = state.isMuted,
            isCurrent = isCurrent,
            onLike = {
                likeBurstFor = pitch.id
                onIntent(FeedIntent.Like(pitch.id))
            },
            onSave = {
                saveBurstFor = pitch.id
                onIntent(FeedIntent.ToggleSave(pitch.id))
            },
            onPass = { onIntent(FeedIntent.Pass(pitch.id)) },
            onToggleMute = { onIntent(FeedIntent.ToggleMute) },
            showLikeBurst = isCurrent && likeBurstFor == pitch.id,
            showBookmarkPulse = isCurrent && saveBurstFor == pitch.id,
        )
    }
}

@Composable
private fun CenterText(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            color = AxiooTokens.palette.cream,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
) {
    val spacing = AxiooTokens.spacing
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.feed_error_title),
            style = MaterialTheme.typography.titleLarge,
            color = AxiooTokens.palette.cream,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = AxiooTokens.palette.cream.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = spacing.s),
        )
        Text(
            text = stringResource(R.string.feed_error_retry).uppercase(),
            color = AxiooTokens.palette.orange,
            style = AxiooTokens.type.monoLabel,
            modifier =
                Modifier
                    .padding(top = spacing.l)
                    .background(AxiooTokens.palette.cream.copy(alpha = 0.0f))
                    .padding(spacing.s),
        )
    }
}

private const val BURST_DURATION_MS: Long = 600L
