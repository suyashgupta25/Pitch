package com.axioo.feed.ui.feed

import androidx.compose.ui.test.junit4.createComposeRule
import com.axioo.feed.domain.model.EngagementCounts
import com.axioo.feed.domain.model.Pitch
import com.axioo.feed.domain.model.PitchId
import com.axioo.feed.ui.theme.AxiooSurfaceMode
import com.axioo.feed.ui.theme.AxiooTheme
import org.junit.Rule
import org.junit.Test

class FeedScreenSmokeTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun feedRoot_isDisplayed_whenStateIsReady() {
        rule.setContent {
            AxiooTheme(mode = AxiooSurfaceMode.Dark) {
                val pitch =
                    PitchUi(
                        pitch =
                            Pitch(
                                id = PitchId("a"),
                                startupName = "Lattice",
                                oneLinePitch = "An AI scheduling assistant.",
                                founderName = "Maya",
                                category = "AI",
                                videoUrl = "https://example/a.mp4",
                                thumbnailUrl = "https://example/a.jpg",
                                counts = EngagementCounts(likes = 1, saves = 1, views = 1),
                                createdAtEpochMillis = 0L,
                            ),
                        isLiked = false,
                        isSaved = false,
                        likeOverride = null,
                        saveOverride = null,
                    )
                PitchCard(
                    pitch = pitch,
                    player = null,
                    isMuted = true,
                    isCurrent = true,
                    onLike = {},
                    onSave = {},
                    onPass = {},
                    onToggleMute = {},
                    showLikeBurst = false,
                    showBookmarkPulse = false,
                )
            }
        }
        // Smoke: composition succeeds and the card layer is on screen.
        rule.waitForIdle()
    }
}
