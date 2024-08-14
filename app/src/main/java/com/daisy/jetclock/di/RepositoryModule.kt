package com.daisy.jetclock.di

import com.daisy.jetclock.repositories.AlarmRepository
import com.daisy.jetclock.repositories.AlarmRepositoryImpl
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