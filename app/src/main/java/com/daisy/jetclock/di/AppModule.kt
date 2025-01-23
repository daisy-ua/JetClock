package com.daisy.jetclock.di

import android.content.Context
import com.daisy.jetclock.core.scheduler.AlarmSchedulerManager
import com.daisy.jetclock.core.scheduler.AlarmSchedulerManagerImpl
import com.daisy.jetclock.core.task.WorkRequestManager
import com.daisy.jetclock.presentation.utils.next.NextAlarmHandler
import com.daisy.jetclock.utils.SoundPoolManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAlarmScheduler(
        @ApplicationContext context: Context,
    ): AlarmSchedulerManager = AlarmSchedulerManagerImpl(context)

    @Provides
    @Singleton
    fun provideWorkRequestManager(
        @ApplicationContext context: Context,
    ): WorkRequestManager = WorkRequestManager(context)

    @Provides
    fun provideSoundPoolManager(): SoundPoolManager = SoundPoolManager()

    @Provides
    fun provideNextAlarmHandler(): NextAlarmHandler = NextAlarmHandler()
}
