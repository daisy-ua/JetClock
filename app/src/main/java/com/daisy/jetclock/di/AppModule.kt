package com.daisy.jetclock.di

import android.content.Context
import com.daisy.jetclock.core.manager.AlarmSchedulerManager
import com.daisy.jetclock.core.manager.AlarmSchedulerManagerImpl
import com.daisy.jetclock.utils.AlarmNotificationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AlarmModule {

    @Provides
    @Singleton
    fun provideAlarmScheduler(
        @ApplicationContext context: Context,
    ): AlarmSchedulerManager = AlarmSchedulerManagerImpl(context)

//    @Provides
//    fun provideAlarmNotificationManager(@ApplicationContext context: Context): AlarmNotificationManager {
//        return AlarmNotificationManager(context)
//    }
}
