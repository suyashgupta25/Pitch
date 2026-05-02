package com.axioo.feed.data.mapper

import com.axioo.feed.data.dto.PitchDto
import com.axioo.feed.domain.model.EngagementCounts
import com.axioo.feed.domain.model.Pitch
import com.axioo.feed.domain.model.PitchId

fun PitchDto.toDomain(): Pitch =
    Pitch(
        id = PitchId(id),
        startupName = startupName,
        oneLinePitch = oneLinePitch,
        founderName = founderName,
        category = category,
        videoUrl = videoUrl,
        thumbnailUrl = thumbnailUrl,
        counts = EngagementCounts(likes = likes, saves = saves, views = views),
        createdAtEpochMillis = createdAtEpochMillis,
    )
