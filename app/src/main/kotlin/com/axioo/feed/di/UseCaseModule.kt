package com.axioo.feed.di

import com.axioo.feed.domain.repository.PitchRepository
import com.axioo.feed.domain.usecase.GetFeedPitchesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideGetFeedPitches(pitchRepository: PitchRepository): GetFeedPitchesUseCase =
        GetFeedPitchesUseCase(pitchRepository)
}
