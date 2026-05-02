package com.axioo.feed.domain.usecase

import com.axioo.feed.domain.model.EngagementCounts
import com.axioo.feed.domain.model.Pitch
import com.axioo.feed.domain.model.PitchId
import com.axioo.feed.domain.repository.PitchRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetFeedPitchesUseCaseTest {
    @Test
    fun `recent pitches with engagement rank above older ones`() =
        runTest {
            val now = 1_000_000_000_000L
            val hour = 60L * 60L * 1000L
            val recentHighEngagement =
                pitch("a", createdAt = now - hour, counts = EngagementCounts(likes = 100, saves = 50, views = 1_000))
            val oldEvenHigherEngagement =
                pitch(
                    "b",
                    createdAt = now - 30 * 24 * hour,
                    counts = EngagementCounts(likes = 800, saves = 200, views = 10_000),
                )
            val brandNewLow =
                pitch("c", createdAt = now, counts = EngagementCounts(likes = 5, saves = 1, views = 50))

            val repo = FakeRepo(listOf(oldEvenHigherEngagement, recentHighEngagement, brandNewLow))
            val useCase = GetFeedPitchesUseCase(repo, nowEpochMillis = { now })

            val result = useCase().first()

            assertThat(result.first().id.value).isEqualTo("a")
            assertThat(result.last().id.value).isEqualTo("b")
        }

    @Test
    fun `empty feed yields empty list`() =
        runTest {
            val useCase = GetFeedPitchesUseCase(FakeRepo(emptyList()))
            assertThat(useCase().first()).isEmpty()
        }

    private fun pitch(
        id: String,
        createdAt: Long,
        counts: EngagementCounts,
    ): Pitch =
        Pitch(
            id = PitchId(id),
            startupName = "Startup $id",
            oneLinePitch = "Pitch $id",
            founderName = "Founder $id",
            category = "AI",
            videoUrl = "",
            thumbnailUrl = "",
            counts = counts,
            createdAtEpochMillis = createdAt,
        )

    private class FakeRepo(
        private val items: List<Pitch>,
    ) : PitchRepository {
        override fun observeFeed() = flowOf(items)

        override suspend fun getById(id: PitchId): Pitch? = items.firstOrNull { it.id == id }
    }
}
