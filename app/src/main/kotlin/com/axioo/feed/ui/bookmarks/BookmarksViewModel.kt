package com.axioo.feed.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axioo.feed.domain.repository.PitchRepository
import com.axioo.feed.domain.repository.UserPrefsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel
    @Inject
    constructor(
        private val pitchRepository: PitchRepository,
        private val userPrefs: UserPrefsRepository,
    ) : ViewModel() {
        val state: StateFlow<BookmarksUiState> =
            combine(
                pitchRepository.observeFeed(),
                userPrefs.savedIds,
            ) { pitches, savedIds ->
                val items = pitches.filter { it.id in savedIds }
                if (items.isEmpty()) BookmarksUiState.Empty else BookmarksUiState.Ready(items.toImmutableList())
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), BookmarksUiState.Loading)

        private companion object {
            const val STOP_TIMEOUT: Long = 5_000L
        }
    }
