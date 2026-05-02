package com.axioo.feed.domain.usecase

import com.axioo.feed.domain.model.Pitch
import com.axioo.feed.domain.repository.PitchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.pow

/**
 * Returns the feed ordered by a trending score blending engagement and recency.
 *
 * Score = (likes + 2 * saves + 0.1 * views) / (hoursOld + 2) ^ 1.5
 *
 * The use case earns its keep because the ordering is non-trivial product policy that should
 * remain stable as the data layer evolves and is shared by Feed and Profile carousels.
 */
class GetFeedPitchesUseCase(
    private val pitchRepository: PitchRepository,
    private val nowEpochMillis: () -> Long = System::currentTimeMillis,
) {
    operator fun invoke(): Flow<List<Pitch>> =
        pitchRepository.observeFeed().map { pitches ->
            val now = nowEpochMillis()
            pitches
                .map { it to score(it, now) }
                .sortedByDescending { it.second }
                .map { it.first }
        }

    private fun score(
        pitch: Pitch,
        now: Long,
    ): Double {
        val hoursOld = ((now - pitch.createdAtEpochMillis).coerceAtLeast(0L)) / MILLIS_PER_HOUR.toDouble()
        val engagement =
            pitch.counts.likes +
                LIKE_WEIGHT_FOR_SAVES * pitch.counts.saves +
                VIEW_WEIGHT * pitch.counts.views
        return engagement / (hoursOld + RECENCY_PIVOT_HOURS).pow(RECENCY_DECAY)
    }

    private companion object {
        const val MILLIS_PER_HOUR: Long = 60L * 60L * 1000L
        const val LIKE_WEIGHT_FOR_SAVES: Double = 2.0
        const val VIEW_WEIGHT: Double = 0.1
        const val RECENCY_PIVOT_HOURS: Double = 2.0
        const val RECENCY_DECAY: Double = 1.5
    }
}
