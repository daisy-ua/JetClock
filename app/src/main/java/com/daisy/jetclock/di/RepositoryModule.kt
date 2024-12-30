package com.daisy.jetclock.di

import com.daisy.jetclock.core.alarm.AlarmAction
import com.daisy.jetclock.core.alarm.AlarmCoordinator
import com.daisy.jetclock.core.media.MediaPlaybackServiceImpl
import com.daisy.jetclock.core.media.PlaybackService
import com.daisy.jetclock.core.receiver.MediaPlaybackHandler
import com.daisy.jetclock.core.receiver.ServiceHandler
import com.daisy.jetclock.domain.repository.AlarmRepository
import com.daisy.jetclock.data.repository.AlarmRepositoryImpl
import com.daisy.jetclock.data.repository.SoundRepositoryImpl
import com.daisy.jetclock.domain.repository.SoundRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindAlarmRepository(
        impl: AlarmRepositoryImpl,
    ): AlarmRepository

    @Binds
    @Singleton
    fun bindSoundRepository(
        impl: SoundRepositoryImpl,
    ): SoundRepository

    @Binds
    @Singleton
    fun bindAlarmAction(
        impl: AlarmCoordinator,
    ): AlarmAction

    @Binds
    @Singleton
    fun bindServiceHandler(
        impl: MediaPlaybackHandler,
    ): ServiceHandler<Long>

    @Binds
    @Singleton
    fun bindPlaybackService(
        impl: MediaPlaybackServiceImpl,
    ): PlaybackService
}