package com.axioo.feed.ui.feed

import com.axioo.feed.domain.model.Pitch
import com.axioo.feed.domain.model.PitchId
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

data class PitchUi(
    val pitch: Pitch,
    val isLiked: Boolean,
    val isSaved: Boolean,
    val likeOverride: Long?,
    val saveOverride: Long?,
) {
    val id: PitchId get() = pitch.id
    val displayLikes: Long get() = likeOverride ?: pitch.counts.likes
    val displaySaves: Long get() = saveOverride ?: pitch.counts.saves
}

sealed interface FeedUiState {
    data object Loading : FeedUiState

    data class Error(
        val message: String,
    ) : FeedUiState

    data class Ready(
        val pitches: ImmutableList<PitchUi>,
        val currentIndex: Int,
        val isMuted: Boolean,
        val passedIds: ImmutableList<PitchId>,
    ) : FeedUiState {
        companion object {
            val Empty =
                Ready(
                    pitches = persistentListOf(),
                    currentIndex = 0,
                    isMuted = true,
                    passedIds = persistentListOf(),
                )
        }
    }
}

internal fun List<PitchUi>.toImmutable(): ImmutableList<PitchUi> = this.toImmutableList()
