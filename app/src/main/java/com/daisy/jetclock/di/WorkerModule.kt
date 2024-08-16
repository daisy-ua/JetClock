package com.daisy.jetclock.di

import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import com.daisy.jetclock.core.worker.RescheduleAlarmWorker
import com.daisy.jetclock.core.worker.factory.AssistedWorkerFactory
import com.daisy.jetclock.core.worker.factory.RescheduleAlarmWorkerFactory
import com.daisy.jetclock.core.worker.factory.WrapperWorkerFactory
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WorkerKey(val value: KClass<out ListenableWorker>)

@Module
@InstallIn(SingletonComponent::class)
interface WorkerModule {

    @Binds
    fun bindWorkerFactoryModule(workerFactory: WrapperWorkerFactory): WorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(RescheduleAlarmWorker::class)
    fun bindRescheduleAlarmWorker(rescheduleAlarmWorkerFactory: RescheduleAlarmWorkerFactory): AssistedWorkerFactory
}
