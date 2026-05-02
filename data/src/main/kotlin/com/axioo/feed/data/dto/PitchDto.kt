@file:OptIn(InternalSerializationApi::class)

package com.axioo.feed.data.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PitchDto(
    @SerialName("id") val id: String,
    @SerialName("startupName") val startupName: String,
    @SerialName("oneLinePitch") val oneLinePitch: String,
    @SerialName("founderName") val founderName: String,
    @SerialName("category") val category: String,
    @SerialName("videoUrl") val videoUrl: String,
    @SerialName("thumbnailUrl") val thumbnailUrl: String,
    @SerialName("likes") val likes: Long,
    @SerialName("saves") val saves: Long,
    @SerialName("views") val views: Long,
    @SerialName("createdAt") val createdAtEpochMillis: Long,
)

@Serializable
data class PitchesEnvelopeDto(
    @SerialName("pitches") val pitches: List<PitchDto>,
)
