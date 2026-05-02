package com.axioo.feed.ui.profile

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
class ProfileViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before fun setUp() = Dispatchers.setMain(dispatcher)

    @After fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `setting user type writes to prefs`() =
        runTest(dispatcher) {
            val prefs = FakePrefs()
            val vm = ProfileViewModel(FakePitches(emptyList()), prefs)
            vm.onIntent(ProfileIntent.SetUserType(UserType.Founder))
            advanceUntilIdle()
            assertThat(prefs.userTypeFlow.value).isEqualTo(UserType.Founder)
        }

    @Test
    fun `liked and saved pitches surface in carousels`() =
        runTest(dispatcher) {
            val pitches = listOf(samplePitch("a"), samplePitch("b"), samplePitch("c"))
            val prefs =
                FakePrefs().apply {
                    likedFlow.value = setOf(PitchId("a"))
                    savedFlow.value = setOf(PitchId("b"), PitchId("c"))
                }
            val vm = ProfileViewModel(FakePitches(pitches), prefs)

            vm.state.test {
                awaitItem() // initial empty
                advanceUntilIdle()
                val ready = awaitItem()
                assertThat(ready.liked.map { it.id.value }).containsExactly("a")
                assertThat(ready.saved.map { it.id.value }).containsExactly("b", "c")
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
        val likedFlow = MutableStateFlow<Set<PitchId>>(emptySet())
        val savedFlow = MutableStateFlow<Set<PitchId>>(emptySet())
        val userTypeFlow = MutableStateFlow(UserType.Investor)

        override val likedIds = likedFlow
        override val savedIds = savedFlow
        override val userType = userTypeFlow

        override suspend fun setUserType(type: UserType) {
            userTypeFlow.value = type
        }

        override suspend fun setLiked(
            id: PitchId,
            liked: Boolean,
        ) {}

        override suspend fun toggleSaved(id: PitchId) {}
    }
}
