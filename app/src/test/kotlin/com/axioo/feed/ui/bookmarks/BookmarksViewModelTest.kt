package com.axioo.feed.ui.bookmarks

import app.cash.turbine.test
import com.axioo.feed.domain.model.EngagementCounts
import com.axioo.feed.domain.model.Pitch
import com.axioo.feed.domain.model.PitchId
import com.axioo.feed.domain.model.UserType
import com.axioo.feed.domain.repository.PitchRepository
import com.axioo.feed.domain.repository.UserPrefsRepository
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
class BookmarksViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before fun setUp() = Dispatchers.setMain(dispatcher)

    @After fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `empty saved set yields Empty`() =
        runTest(dispatcher) {
            val vm = BookmarksViewModel(FakePitches(listOf(samplePitch("a"))), FakePrefs())
            vm.state.test {
                awaitItem() // Loading
                val state = awaitItem()
                assertThat(state).isInstanceOf(BookmarksUiState.Empty::class.java)
            }
        }

    @Test
    fun `saved id appears in Ready items`() =
        runTest(dispatcher) {
            val prefs = FakePrefs().apply { savedFlow.value = setOf(PitchId("a")) }
            val vm = BookmarksViewModel(FakePitches(listOf(samplePitch("a"))), prefs)

            vm.state.test {
                awaitItem() // Loading
                advanceUntilIdle()
                val state = awaitItem() as BookmarksUiState.Ready
                assertThat(state.items.map { it.id.value }).containsExactly("a")
            }
        }

    private fun samplePitch(id: String) =
        Pitch(
            id = PitchId(id),
            startupName = "S$id",
            oneLinePitch = "P$id",
            founderName = "F$id",
            category = "AI",
            videoUrl = "",
            thumbnailUrl = "",
            counts = EngagementCounts(0, 0, 0),
            createdAtEpochMillis = 0L,
        )

    private class FakePitches(
        private val items: List<Pitch>,
    ) : PitchRepository {
        override fun observeFeed() = flowOf(items)

        override suspend fun getById(id: PitchId): Pitch? = items.firstOrNull { it.id == id }
    }

    private class FakePrefs : UserPrefsRepository {
        val savedFlow = MutableStateFlow<Set<PitchId>>(emptySet())
        override val likedIds = MutableStateFlow<Set<PitchId>>(emptySet())
        override val savedIds = savedFlow
        override val userType = MutableStateFlow(UserType.Investor)

        override suspend fun setUserType(type: UserType) {}

        override suspend fun setLiked(
            id: PitchId,
            liked: Boolean,
        ) {}

        override suspend fun toggleSaved(id: PitchId) {}
    }
}
