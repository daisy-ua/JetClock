package com.daisy.jetclock.di

import com.daisy.jetclock.core.alarm.AlarmAction
import com.daisy.jetclock.core.alarm.AlarmActionHandler
import com.daisy.jetclock.core.media.MediaPlaybackServiceImpl
import com.daisy.jetclock.core.media.PlaybackService
import com.daisy.jetclock.core.notification.foreground.ForegroundServiceNotification
import com.daisy.jetclock.core.notification.foreground.ForegroundServiceNotificationImpl
import com.daisy.jetclock.core.notification.fullscreen.FullscreenNotificationStopper
import com.daisy.jetclock.core.notification.fullscreen.OngoingAlarmFullscreenNotificationHandler
import com.daisy.jetclock.core.receiver.AlarmServiceHandler
import com.daisy.jetclock.core.receiver.ServiceHandler
import com.daisy.jetclock.data.repository.AlarmRepositoryImpl
import com.daisy.jetclock.data.repository.SoundRepositoryImpl
import com.daisy.jetclock.data.repository.UISettingsRepositoryImpl
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.repository.AlarmRepository
import com.daisy.jetclock.domain.repository.SoundRepository
import com.daisy.jetclock.domain.repository.UISettingsRepository
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
    fun bindUISettingsRepository(
        impl: UISettingsRepositoryImpl,
    ): UISettingsRepository

    @Binds
    @Singleton
    fun bindAlarmAction(
        impl: AlarmActionHandler,
    ): AlarmAction

    @Binds
    @Singleton
    fun bindServiceHandler(
        impl: AlarmServiceHandler,
    ): ServiceHandler<Long>

    @Binds
    @Singleton
    fun bindPlaybackService(
        impl: MediaPlaybackServiceImpl,
    ): PlaybackService

    @Binds
    @Singleton
    fun bindFullscreenNotificationStopper(
        impl: OngoingAlarmFullscreenNotificationHandler,
    ): FullscreenNotificationStopper

    @Binds
    @Singleton
    fun bindForegroundServiceNotification(
        impl: ForegroundServiceNotificationImpl,
    ): ForegroundServiceNotification<Alarm>
}