package com.axioo.feed.ui.profile

import com.axioo.feed.domain.model.Pitch
import com.axioo.feed.domain.model.UserType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ProfileUiState(
    val userType: UserType = UserType.Investor,
    val liked: ImmutableList<Pitch> = persistentListOf(),
    val saved: ImmutableList<Pitch> = persistentListOf(),
)

sealed interface ProfileIntent {
    data class SetUserType(
        val type: UserType,
    ) : ProfileIntent
}
