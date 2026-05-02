package com.axioo.feed.ui.feed

import com.axioo.feed.domain.model.PitchId

sealed interface FeedIntent {
    data class PageChanged(
        val index: Int,
    ) : FeedIntent

    data class Like(
        val id: PitchId,
    ) : FeedIntent

    data class Pass(
        val id: PitchId,
    ) : FeedIntent

    data class ToggleSave(
        val id: PitchId,
    ) : FeedIntent

    data object ToggleMute : FeedIntent

    data object Retry : FeedIntent
}
