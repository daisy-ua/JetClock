package com.daisy.jetclock.core.manager

import android.content.Context
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WorkRequestManager @Inject constructor(
    @ApplicationContext val context: Context,
) {

    inline fun <reified T : ListenableWorker> enqueueWorker(tag: String, inputData: Data? = null) {
        val workRequest = OneTimeWorkRequestBuilder<T>().addTag(tag)

        inputData?.let {
            workRequest.setInputData(it)
        }

        WorkManager.getInstance(context).enqueue(workRequest.build())
    }

    fun cancelWorker(tag: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(tag)
    }
}