package com.axioo.feed.ui.feed.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import com.axioo.feed.domain.model.PitchId

/**
 * Owns three ExoPlayer instances bound to the previous, current, and next page in the feed.
 *
 * Why three: the visible page must play instantly; the next page must be pre-buffered so landing
 * on it never shows a spinner; the previous page is kept warm for back-swipes. Going beyond three
 * burns hardware decoders for no perceptual gain.
 *
 * The pool is owned by the screen-level composable. On lifecycle stop the screen calls [release];
 * the pool is rebuilt cheaply on the next entry.
 */
@OptIn(UnstableApi::class)
class PitchPlayerPool private constructor(
    private val context: Context,
    val capacity: Int,
) {
    private data class Slot(
        val player: ExoPlayer,
        var pitchId: PitchId? = null,
        var url: String? = null,
    )

    private val slots: MutableList<Slot> = mutableListOf()
    private var muted: Boolean = true

    init {
        repeat(capacity) {
            slots += Slot(buildPlayer())
        }
    }

    private fun buildPlayer(): ExoPlayer {
        val loadControl: LoadControl =
            DefaultLoadControl
                .Builder()
                .setBufferDurationsMs(
                    MIN_BUFFER_MS,
                    MAX_BUFFER_MS,
                    BUFFER_FOR_PLAYBACK_MS,
                    BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS,
                ).build()
        return ExoPlayer
            .Builder(context)
            .setLoadControl(loadControl)
            .build()
            .apply {
                volume = if (muted) 0f else 1f
                repeatMode = Player.REPEAT_MODE_ONE
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            }
    }

    /**
     * Bind the window of pages [prev, current, next] to the pool, returning the player for the
     * currently visible page so callers can pass it to [VideoSurface].
     */
    fun bind(window: PitchWindow): Map<PitchId, Player> {
        val desired = window.entries.filterNotNull()
        val pinned = mutableMapOf<PitchId, Slot>()

        // Reuse existing slots whose pitchId is still in the window.
        val freeSlots = slots.toMutableList()
        for ((id, _) in desired) {
            val existing = freeSlots.firstOrNull { it.pitchId == id }
            if (existing != null) {
                freeSlots -= existing
                pinned[id] = existing
            }
        }
        // Assign remaining desired entries to free slots, preparing media as needed.
        for ((id, url) in desired) {
            if (id in pinned) continue
            val slot = freeSlots.removeAt(0)
            slot.pitchId = id
            slot.url = url
            slot.player.apply {
                setMediaItem(MediaItem.fromUri(url))
                prepare()
                playWhenReady = (id == window.current?.first)
            }
            pinned[id] = slot
        }
        // Pause the visible-but-not-current players (prev/next).
        for ((id, slot) in pinned) {
            slot.player.playWhenReady = (id == window.current?.first)
        }
        // Slots that aren't in the desired window can stay parked but paused.
        for (slot in slots) {
            if (slot !in pinned.values) {
                slot.player.playWhenReady = false
            }
        }
        return pinned.mapValues { it.value.player }
    }

    fun setMuted(muted: Boolean) {
        this.muted = muted
        slots.forEach { it.player.volume = if (muted) 0f else 1f }
    }

    fun pauseAll() {
        slots.forEach { it.player.playWhenReady = false }
    }

    fun release() {
        slots.forEach { runCatching { it.player.release() } }
        slots.clear()
    }

    companion object {
        const val DEFAULT_CAPACITY: Int = 3

        private const val MIN_BUFFER_MS: Int = 5_000
        private const val MAX_BUFFER_MS: Int = 30_000
        private const val BUFFER_FOR_PLAYBACK_MS: Int = 1_000
        private const val BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS: Int = 2_000

        fun create(
            context: Context,
            capacity: Int = DEFAULT_CAPACITY,
        ): PitchPlayerPool = PitchPlayerPool(context.applicationContext, capacity)
    }
}

data class PitchWindow(
    val previous: Pair<PitchId, String>?,
    val current: Pair<PitchId, String>?,
    val next: Pair<PitchId, String>?,
) {
    val entries: List<Pair<PitchId, String>?> = listOf(previous, current, next)
}
