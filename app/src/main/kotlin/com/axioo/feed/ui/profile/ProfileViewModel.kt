package com.axioo.feed.ui.profile

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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val pitchRepository: PitchRepository,
        private val userPrefs: UserPrefsRepository,
    ) : ViewModel() {
        val state: StateFlow<ProfileUiState> =
            combine(
                pitchRepository.observeFeed(),
                userPrefs.likedIds,
                userPrefs.savedIds,
                userPrefs.userType,
            ) { pitches, likedIds, savedIds, userType ->
                ProfileUiState(
                    userType = userType,
                    liked = pitches.filter { it.id in likedIds }.toImmutableList(),
                    saved = pitches.filter { it.id in savedIds }.toImmutableList(),
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), ProfileUiState())

        fun onIntent(intent: ProfileIntent) {
            when (intent) {
                is ProfileIntent.SetUserType ->
                    viewModelScope.launch {
                        userPrefs.setUserType(intent.type)
                    }
            }
        }

        private companion object {
            const val STOP_TIMEOUT: Long = 5_000L
        }
    }
