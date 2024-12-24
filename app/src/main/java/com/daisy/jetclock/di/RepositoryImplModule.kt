package com.daisy.jetclock.di

import com.daisy.jetclock.data.local.dao.AlarmDao
import com.daisy.jetclock.data.repository.AlarmRepositoryImpl
import com.daisy.jetclock.data.repository.SoundRepositoryImpl
import com.daisy.jetclock.data.source.AssetDataSource
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
    @Singleton
    fun provideSoundRepository(
        assetDataSource: AssetDataSource,
    ): SoundRepositoryImpl = SoundRepositoryImpl(assetDataSource)

    @Provides
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}
