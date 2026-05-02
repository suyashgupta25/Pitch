package com.axioo.feed.domain.repository

import com.axioo.feed.domain.model.PitchId
import com.axioo.feed.domain.model.UserType
import kotlinx.coroutines.flow.Flow

interface UserPrefsRepository {
    val userType: Flow<UserType>
    val likedIds: Flow<Set<PitchId>>
    val savedIds: Flow<Set<PitchId>>

    suspend fun setUserType(type: UserType)

    suspend fun setLiked(
        id: PitchId,
        liked: Boolean,
    )

    suspend fun toggleSaved(id: PitchId)
}
