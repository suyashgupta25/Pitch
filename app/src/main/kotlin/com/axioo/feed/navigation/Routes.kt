package com.axioo.feed.navigation

import kotlinx.serialization.Serializable

sealed interface AxiooRoute

@Serializable
data object FeedRoute : AxiooRoute

@Serializable
data object BookmarksRoute : AxiooRoute

@Serializable
data object ProfileRoute : AxiooRoute

@Serializable
data class FocusedPitchRoute(
    val pitchId: String,
) : AxiooRoute
