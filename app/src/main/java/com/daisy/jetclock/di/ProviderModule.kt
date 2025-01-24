package com.daisy.jetclock.di

import com.daisy.jetclock.data.provider.SystemTimeProviderImpl
import com.daisy.jetclock.domain.provider.SystemTimeProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface ProviderModule {

    @Binds
    @Singleton
    fun bindSystemTimeProvider(
        impl: SystemTimeProviderImpl,
    ): SystemTimeProvider
}
