package com.axioo.feed.domain.repository

import com.axioo.feed.domain.model.Pitch
import com.axioo.feed.domain.model.PitchId
import kotlinx.coroutines.flow.Flow

interface PitchRepository {
    fun observeFeed(): Flow<List<Pitch>>

    suspend fun getById(id: PitchId): Pitch?
}
