package com.axioo.feed.ui.feed

import app.cash.turbine.test
import com.axioo.feed.domain.model.EngagementCounts
import com.axioo.feed.domain.model.Pitch
import com.axioo.feed.domain.model.PitchId
import com.axioo.feed.domain.model.UserType
import com.axioo.feed.domain.repository.PitchRepository
import com.axioo.feed.domain.repository.UserPrefsRepository
import com.axioo.feed.domain.usecase.GetFeedPitchesUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `pass removes pitch from visible feed`() =
        runTest(dispatcher) {
            val pitches = listOf(samplePitch("a"), samplePitch("b"))
            val vm =
                FeedViewModel(
                    getFeed = GetFeedPitchesUseCase(FakeRepo(pitches), nowEpochMillis = { 0L }),
                    userPrefs = FakePrefs(),
                )

            vm.state.test {
                // Skip Loading
                awaitItem()
                val ready = awaitItem() as FeedUiState.Ready
                assertThat(ready.pitches).hasSize(2)

                vm.onIntent(FeedIntent.Pass(PitchId("a")))
                advanceUntilIdle()
                val after = awaitItem() as FeedUiState.Ready
                assertThat(after.pitches.map { it.id.value }).containsExactly("b")
            }
        }

    @Test
    fun `like flips state and bumps display count`() =
        runTest(dispatcher) {
            val prefs = FakePrefs()
            val pitches = listOf(samplePitch("a", likes = 10))
            val vm =
                FeedViewModel(
                    getFeed = GetFeedPitchesUseCase(FakeRepo(pitches), nowEpochMillis = { 0L }),
                    userPrefs = prefs,
                )

            vm.state.test {
                awaitItem()
                awaitItem() // Ready
                vm.onIntent(FeedIntent.Like(PitchId("a")))
                advanceUntilIdle()
                val ready = awaitItem() as FeedUiState.Ready
                val pitch = ready.pitches.first()
                assertThat(pitch.isLiked).isTrue()
                assertThat(pitch.displayLikes).isEqualTo(11L)
            }
        }

    @Test
    fun `toggle mute flips muted state`() =
        runTest(dispatcher) {
            val pitches = listOf(samplePitch("a"))
            val vm =
                FeedViewModel(
                    getFeed = GetFeedPitchesUseCase(FakeRepo(pitches), nowEpochMillis = { 0L }),
                    userPrefs = FakePrefs(),
                )
            vm.state.test {
                awaitItem()
                val first = awaitItem() as FeedUiState.Ready
                assertThat(first.isMuted).isTrue()
                vm.onIntent(FeedIntent.ToggleMute)
                advanceUntilIdle()
                val next = awaitItem() as FeedUiState.Ready
                assertThat(next.isMuted).isFalse()
            }
        }

    private fun samplePitch(
        id: String,
        likes: Long = 0L,
    ) = Pitch(
        id = PitchId(id),
        startupName = "S$id",
        oneLinePitch = "P$id",
        founderName = "F$id",
        category = "AI",
        videoUrl = "https://example/$id.mp4",
        thumbnailUrl = "https://example/$id.jpg",
        counts = EngagementCounts(likes = likes, saves = 0, views = 0),
        createdAtEpochMillis = 0L,
    )

    private class FakeRepo(
        private val items: List<Pitch>,
    ) : PitchRepository {
        override fun observeFeed() = flowOf(items)

        override suspend fun getById(id: PitchId): Pitch? = items.firstOrNull { it.id == id }
    }

    private class FakePrefs : UserPrefsRepository {
        private val likes = MutableStateFlow<Set<PitchId>>(emptySet())
        private val saves = MutableStateFlow<Set<PitchId>>(emptySet())
        private val type = MutableStateFlow(UserType.Investor)

        override val userType = type
        override val likedIds = likes
        override val savedIds = saves

        override suspend fun setUserType(typeIn: UserType) {
            type.value = typeIn
        }

        override suspend fun setLiked(
            id: PitchId,
            liked: Boolean,
        ) {
            likes.value = if (liked) likes.value + id else likes.value - id
        }

        override suspend fun toggleSaved(id: PitchId) {
            saves.value = if (id in saves.value) saves.value - id else saves.value + id
        }
    }
}
