package com.daisy.jetclock.di

import android.content.Context
import com.daisy.jetclock.core.manager.AlarmSchedulerManager
import com.daisy.jetclock.core.manager.AlarmSchedulerManagerImpl
import com.daisy.jetclock.core.manager.WorkRequestManager
import com.daisy.jetclock.utils.SoundPoolManager
import com.daisy.jetclock.utils.nextalarm.NextAlarmHandler
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
