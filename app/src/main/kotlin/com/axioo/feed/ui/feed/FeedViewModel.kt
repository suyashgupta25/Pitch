package com.axioo.feed.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axioo.feed.domain.model.PitchId
import com.axioo.feed.domain.repository.UserPrefsRepository
import com.axioo.feed.domain.usecase.GetFeedPitchesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel
    @Inject
    constructor(
        private val getFeed: GetFeedPitchesUseCase,
        private val userPrefs: UserPrefsRepository,
    ) : ViewModel() {
        private data class Local(
            val passed: Set<PitchId> = emptySet(),
            val currentIndex: Int = 0,
            val isMuted: Boolean = true,
            val likeDelta: Map<PitchId, Long> = emptyMap(),
            val saveDelta: Map<PitchId, Long> = emptyMap(),
            val error: String? = null,
        )

        private val local = MutableStateFlow(Local())

        val state: StateFlow<FeedUiState> =
            combine(
                getFeed()
                    .onStart { local.update { it.copy(error = null) } }
                    .catch { e -> local.update { it.copy(error = e.message ?: "Unknown error") } },
                userPrefs.likedIds,
                userPrefs.savedIds,
                local,
            ) { pitches, liked, saved, l ->
                l.error?.let { return@combine FeedUiState.Error(it) }

                val visible = pitches.filterNot { it.id in l.passed }
                val ui =
                    visible.map { p ->
                        val likeBoost = l.likeDelta[p.id] ?: 0L
                        val saveBoost = l.saveDelta[p.id] ?: 0L
                        PitchUi(
                            pitch = p,
                            isLiked = p.id in liked,
                            isSaved = p.id in saved,
                            likeOverride = if (likeBoost != 0L) p.counts.likes + likeBoost else null,
                            saveOverride = if (saveBoost != 0L) p.counts.saves + saveBoost else null,
                        )
                    }
                FeedUiState.Ready(
                    pitches = ui.toImmutableList(),
                    currentIndex = l.currentIndex.coerceIn(0, (ui.size - 1).coerceAtLeast(0)),
                    isMuted = l.isMuted,
                    passedIds = l.passed.toList().toPersistentList(),
                )
            }.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(stopTimeoutMillis = STOP_TIMEOUT),
                FeedUiState.Loading,
            )

        fun onIntent(intent: FeedIntent) {
            when (intent) {
                is FeedIntent.PageChanged -> local.update { it.copy(currentIndex = intent.index) }
                is FeedIntent.Like -> like(intent.id)
                is FeedIntent.Pass -> local.update { it.copy(passed = it.passed + intent.id) }
                is FeedIntent.ToggleSave -> toggleSave(intent.id)
                FeedIntent.ToggleMute -> local.update { it.copy(isMuted = !it.isMuted) }
                FeedIntent.Retry -> local.update { it.copy(error = null) }
            }
        }

        private fun like(id: PitchId) {
            viewModelScope.launch {
                val ready = state.value as? FeedUiState.Ready
                val alreadyLiked = ready?.pitches?.firstOrNull { it.id == id }?.isLiked == true
                userPrefs.setLiked(id, liked = !alreadyLiked)
                local.update { l ->
                    val current = l.likeDelta.toMutableMap()
                    val next = (current[id] ?: 0L) + if (!alreadyLiked) 1L else -1L
                    if (next == 0L) current.remove(id) else current[id] = next
                    l.copy(likeDelta = current)
                }
            }
        }

        private fun toggleSave(id: PitchId) {
            viewModelScope.launch {
                val ready = state.value as? FeedUiState.Ready
                val alreadySaved = ready?.pitches?.firstOrNull { it.id == id }?.isSaved == true
                userPrefs.toggleSaved(id)
                local.update { l ->
                    val current = l.saveDelta.toMutableMap()
                    val next = (current[id] ?: 0L) + if (!alreadySaved) 1L else -1L
                    if (next == 0L) current.remove(id) else current[id] = next
                    l.copy(saveDelta = current)
                }
            }
        }

        private companion object {
            const val STOP_TIMEOUT: Long = 5_000L
        }
    }
