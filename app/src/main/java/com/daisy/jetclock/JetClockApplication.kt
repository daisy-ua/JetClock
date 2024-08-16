package com.daisy.jetclock

import android.app.Application
import androidx.work.Configuration
import com.daisy.jetclock.core.worker.factory.WrapperWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class JetClockApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: WrapperWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}