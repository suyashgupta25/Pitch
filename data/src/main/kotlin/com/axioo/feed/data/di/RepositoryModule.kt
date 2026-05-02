package com.axioo.feed.data.di

import com.axioo.feed.data.repository.PitchRepositoryImpl
import com.axioo.feed.data.repository.UserPrefsRepositoryImpl
import com.axioo.feed.data.util.DefaultDispatcherProvider
import com.axioo.feed.domain.repository.PitchRepository
import com.axioo.feed.domain.repository.UserPrefsRepository
import com.axioo.feed.domain.util.DispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPitchRepository(impl: PitchRepositoryImpl): PitchRepository

    @Binds
    @Singleton
    abstract fun bindUserPrefsRepository(impl: UserPrefsRepositoryImpl): UserPrefsRepository

    @Binds
    @Singleton
    abstract fun bindDispatcherProvider(impl: DefaultDispatcherProvider): DispatcherProvider
}
