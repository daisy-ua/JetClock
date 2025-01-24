package com.daisy.jetclock.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.daisy.jetclock.data.local.dao.AlarmDao
import com.daisy.jetclock.data.repository.AlarmRepositoryImpl
import com.daisy.jetclock.data.repository.SoundRepositoryImpl
import com.daisy.jetclock.data.repository.UISettingsRepositoryImpl
import com.daisy.jetclock.data.source.AssetDataSource
import com.daisy.jetclock.domain.provider.SystemTimeProvider
import com.daisy.jetclock.domain.usecase.GetTimeFormatUseCase
import com.daisy.jetclock.utils.scope.CoroutineScopeProvider
import com.daisy.jetclock.utils.scope.MainCoroutineScopeProvider
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
        getTimeFormatUseCase: GetTimeFormatUseCase,
        ioDispatcher: CoroutineDispatcher,
    ): AlarmRepositoryImpl = AlarmRepositoryImpl(alarmDao, getTimeFormatUseCase, ioDispatcher)

    @Provides
    @Singleton
    fun provideSoundRepository(
        assetDataSource: AssetDataSource,
    ): SoundRepositoryImpl = SoundRepositoryImpl(assetDataSource)

    @Provides
    @Singleton
    fun provideUISettingsRepository(
        dataStore: DataStore<Preferences>,
        systemTimeProvider: SystemTimeProvider,
    ): UISettingsRepositoryImpl = UISettingsRepositoryImpl(dataStore, systemTimeProvider)

    @Provides
    @Singleton
    fun provideCoroutineScopeProvider(): CoroutineScopeProvider {
        return MainCoroutineScopeProvider()
    }

    @Provides
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}
