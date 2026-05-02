package com.axioo.feed.data.local

import android.content.Context
import com.axioo.feed.data.dto.PitchDto
import com.axioo.feed.data.dto.PitchesEnvelopeDto
import com.axioo.feed.domain.util.DispatcherProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockPitchDataSource
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val dispatchers: DispatcherProvider,
        private val json: Json,
    ) {
        private val mutex = Mutex()
        private var cached: List<PitchDto>? = null

        suspend fun load(): List<PitchDto> {
            cached?.let { return it }
            return mutex.withLock {
                cached ?: readFromAssets().also { cached = it }
            }
        }

        private suspend fun readFromAssets(): List<PitchDto> =
            withContext(dispatchers.io) {
                context.assets.open(ASSET_FILE).bufferedReader().use { reader ->
                    json.decodeFromString(PitchesEnvelopeDto.serializer(), reader.readText()).pitches
                }
            }

        private companion object {
            const val ASSET_FILE = "pitches.json"
        }
    }
