package com.axioo.feed.ui.bookmarks

import com.axioo.feed.domain.model.Pitch
import kotlinx.collections.immutable.ImmutableList

sealed interface BookmarksUiState {
    data object Loading : BookmarksUiState

    data object Empty : BookmarksUiState

    data class Ready(
        val items: ImmutableList<Pitch>,
    ) : BookmarksUiState
}
