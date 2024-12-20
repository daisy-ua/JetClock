package com.daisy.jetclock.di

import android.content.Context
import androidx.room.Room
import com.daisy.jetclock.data.local.dao.AlarmDao
import com.daisy.jetclock.data.local.database.LocalDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): LocalDatabase {
//        appContext.deleteDatabase("jetclock.db")
        return Room.databaseBuilder(
            appContext,
            LocalDatabase::class.java,
            "jetclock.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAlarmDao(database: LocalDatabase): AlarmDao = database.alarmDao()
}