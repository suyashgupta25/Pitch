package com.axioo.feed.domain.model

data class Pitch(
    val id: PitchId,
    val startupName: String,
    val oneLinePitch: String,
    val founderName: String,
    val category: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val counts: EngagementCounts,
    val createdAtEpochMillis: Long,
)
