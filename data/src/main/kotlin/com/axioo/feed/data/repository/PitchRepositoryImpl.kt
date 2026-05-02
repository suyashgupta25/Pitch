package com.axioo.feed.data.repository

import com.axioo.feed.data.local.MockPitchDataSource
import com.axioo.feed.data.mapper.toDomain
import com.axioo.feed.domain.model.Pitch
import com.axioo.feed.domain.model.PitchId
import com.axioo.feed.domain.repository.PitchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PitchRepositoryImpl
    @Inject
    constructor(
        private val source: MockPitchDataSource,
    ) : PitchRepository {
        override fun observeFeed(): Flow<List<Pitch>> =
            flow {
                emit(source.load().map { it.toDomain() })
            }

        override suspend fun getById(id: PitchId): Pitch? = source.load().firstOrNull { it.id == id.value }?.toDomain()
    }
