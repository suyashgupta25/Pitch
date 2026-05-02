package com.axioo.feed.data.mapper

import com.axioo.feed.data.dto.PitchDto
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PitchMapperTest {
    @Test
    fun `dto fields map onto domain pitch`() {
        val dto =
            PitchDto(
                id = "p1",
                startupName = "Lattice",
                oneLinePitch = "Calendar that thinks for you.",
                founderName = "A. Founder",
                category = "AI",
                videoUrl = "https://x/video.mp4",
                thumbnailUrl = "https://x/thumb.jpg",
                likes = 12,
                saves = 3,
                views = 144,
                createdAtEpochMillis = 1_700_000_000_000L,
            )

        val pitch = dto.toDomain()

        assertThat(pitch.id.value).isEqualTo("p1")
        assertThat(pitch.startupName).isEqualTo("Lattice")
        assertThat(pitch.counts.likes).isEqualTo(12)
        assertThat(pitch.counts.saves).isEqualTo(3)
        assertThat(pitch.counts.views).isEqualTo(144)
        assertThat(pitch.createdAtEpochMillis).isEqualTo(1_700_000_000_000L)
    }
}
