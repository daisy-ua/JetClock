package com.daisy.jetclock.di

import com.daisy.jetclock.repositories.AlarmRepository
import com.daisy.jetclock.repositories.AlarmRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@InstallIn(ViewModelComponent::class)
@Module
interface RepositoryModule {
    @Binds
    @ViewModelScoped
    fun bindAlarmRepository(
        impl: AlarmRepositoryImpl,
    ): AlarmRepository
}