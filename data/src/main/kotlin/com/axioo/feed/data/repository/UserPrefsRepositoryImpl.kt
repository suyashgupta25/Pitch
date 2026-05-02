package com.axioo.feed.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.axioo.feed.data.local.UserPrefsKeys
import com.axioo.feed.domain.model.PitchId
import com.axioo.feed.domain.model.UserType
import com.axioo.feed.domain.repository.UserPrefsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPrefsRepositoryImpl
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : UserPrefsRepository {
        override val userType: Flow<UserType> =
            dataStore.data.map { prefs ->
                UserType.fromStorage(prefs[UserPrefsKeys.UserType])
            }

        override val likedIds: Flow<Set<PitchId>> =
            dataStore.data.map { prefs ->
                prefs[UserPrefsKeys.LikedIds].orEmpty().mapTo(mutableSetOf(), ::PitchId)
            }

        override val savedIds: Flow<Set<PitchId>> =
            dataStore.data.map { prefs ->
                prefs[UserPrefsKeys.SavedIds].orEmpty().mapTo(mutableSetOf(), ::PitchId)
            }

        override suspend fun setUserType(type: UserType) {
            dataStore.edit { it[UserPrefsKeys.UserType] = type.name }
        }

        override suspend fun setLiked(
            id: PitchId,
            liked: Boolean,
        ) {
            dataStore.edit { prefs ->
                val current = prefs[UserPrefsKeys.LikedIds].orEmpty().toMutableSet()
                if (liked) current += id.value else current -= id.value
                prefs[UserPrefsKeys.LikedIds] = current
            }
        }

        override suspend fun toggleSaved(id: PitchId) {
            dataStore.edit { prefs ->
                val current = prefs[UserPrefsKeys.SavedIds].orEmpty().toMutableSet()
                if (id.value in current) current -= id.value else current += id.value
                prefs[UserPrefsKeys.SavedIds] = current
            }
        }

        suspend fun snapshot(): Snapshot {
            val prefs = dataStore.data.first()
            return Snapshot(
                userType = UserType.fromStorage(prefs[UserPrefsKeys.UserType]),
                likedIds = prefs[UserPrefsKeys.LikedIds].orEmpty().mapTo(mutableSetOf(), ::PitchId),
                savedIds = prefs[UserPrefsKeys.SavedIds].orEmpty().mapTo(mutableSetOf(), ::PitchId),
            )
        }

        data class Snapshot(
            val userType: UserType,
            val likedIds: Set<PitchId>,
            val savedIds: Set<PitchId>,
        )
    }
