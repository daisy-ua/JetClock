package com.daisy.jetclock.di

import com.daisy.jetclock.localdata.dao.AlarmDao
import com.daisy.jetclock.repositories.AlarmRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryImplModule {

    @Provides
    @Singleton
    fun provideAlarmRepository(
        alarmDao: AlarmDao,
        ioDispatcher: CoroutineDispatcher,
    ): AlarmRepositoryImpl = AlarmRepositoryImpl(alarmDao, ioDispatcher)

    @Provides
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}
