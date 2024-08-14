package com.daisy.jetclock.alarmscheduler.worker

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.daisy.jetclock.alarmscheduler.utils.ALARM_WORKER_NOTIFICATION_ID
import com.daisy.jetclock.alarmscheduler.utils.AlarmNotificationManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

@HiltWorker
class AlarmWorker @AssistedInject constructor(
    @Assisted private val notificationManager: AlarmNotificationManager,
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val title = inputData.getString("LABEL") ?: ""
        val time = inputData.getString("TIME") ?: "3:45 pm"
        val notification = notificationManager.getAlarmNotification(title, time).build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                ALARM_WORKER_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            ForegroundInfo(ALARM_WORKER_NOTIFICATION_ID, notification)
        }
    }

    override suspend fun doWork(): Result {
        return try {
            val foregroundInfo = getForegroundInfo()
            setForeground(foregroundInfo)

            delay(1000L)

            Result.success()
        } catch (e: CancellationException) {
            notificationManager.removeAlarmWorkerNotification()
            Result.failure()
        }
    }
}


const val ALARM_TAG = "alarmTag"
