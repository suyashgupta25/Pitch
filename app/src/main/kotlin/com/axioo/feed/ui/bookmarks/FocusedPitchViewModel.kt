package com.axioo.feed.ui.bookmarks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axioo.feed.domain.model.Pitch
import com.axioo.feed.domain.model.PitchId
import com.axioo.feed.domain.repository.PitchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FocusedPitchUiState {
    data object Loading : FocusedPitchUiState

    data class NotFound(
        val id: String,
    ) : FocusedPitchUiState

    data class Ready(
        val pitch: Pitch,
        val isMuted: Boolean,
    ) : FocusedPitchUiState
}

@HiltViewModel
class FocusedPitchViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val pitchRepository: PitchRepository,
    ) : ViewModel() {
        private val pitchId: String = savedStateHandle.get<String>("pitchId").orEmpty()
        private val _state = MutableStateFlow<FocusedPitchUiState>(FocusedPitchUiState.Loading)
        val state: StateFlow<FocusedPitchUiState> = _state.asStateFlow()

        init {
            viewModelScope.launch {
                val pitch = pitchRepository.getById(PitchId(pitchId))
                _state.value =
                    if (pitch == null) {
                        FocusedPitchUiState.NotFound(pitchId)
                    } else {
                        FocusedPitchUiState.Ready(pitch, isMuted = true)
                    }
            }
        }

        fun toggleMute() {
            val current = _state.value as? FocusedPitchUiState.Ready ?: return
            _state.value = current.copy(isMuted = !current.isMuted)
        }
    }
