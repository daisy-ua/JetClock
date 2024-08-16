package com.daisy.jetclock.core.worker.factory

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

interface AssistedWorkerFactory {
    fun create(appContext: Context, workerParams: WorkerParameters): ListenableWorker
}