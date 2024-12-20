package com.daisy.jetclock.di

import com.daisy.jetclock.domain.repository.AlarmRepository
import com.daisy.jetclock.data.repository.AlarmRepositoryImpl
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
}