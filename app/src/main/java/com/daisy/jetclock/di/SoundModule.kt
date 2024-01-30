package com.daisy.jetclock.di

import com.daisy.jetclock.utils.SoundPoolManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SoundModule {

    @Provides
    fun provideSoundPoolManager(): SoundPoolManager {
        return SoundPoolManager()
    }
}