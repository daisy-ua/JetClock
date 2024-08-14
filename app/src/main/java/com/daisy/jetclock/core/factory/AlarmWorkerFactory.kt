package com.daisy.jetclock.alarmscheduler.factory

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.daisy.jetclock.alarmscheduler.utils.AlarmNotificationManager
import com.daisy.jetclock.alarmscheduler.worker.AlarmWorker
import javax.inject.Inject

class AlarmWorkerFactory @Inject constructor(
    private val notificationManager: AlarmNotificationManager,
) : ChildWorkerFactory {

    override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
        return AlarmWorker(notificationManager, appContext, params)
    }
}
