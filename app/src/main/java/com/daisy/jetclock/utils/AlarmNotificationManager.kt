package com.daisy.jetclock.utils

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.daisy.jetclock.R
import com.daisy.jetclock.core.receiver.AlarmBroadcastReceiver
import com.daisy.jetclock.core.receiver.AlarmReceiverActions.ACTION_DISMISS
import com.daisy.jetclock.core.receiver.AlarmReceiverActions.ACTION_SNOOZE
import com.daisy.jetclock.core.receiver.AlarmReceiverActions.DISMISS_REQUEST_CODE
import com.daisy.jetclock.core.receiver.AlarmReceiverActions.SNOOZE_REQUEST_CODE
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val notificationManager = NotificationManagerCompat.from(context)

    private val alarmBroadcastReceiver = AlarmBroadcastReceiver::class.java

    private val dismissIntentAction = alarmBroadcastReceiver.setIntentAction(
        actionName = ACTION_DISMISS,
        requestCode = DISMISS_REQUEST_CODE,
        context = context
    )

    private val snoozeIntentAction = alarmBroadcastReceiver.setIntentAction(
        actionName = ACTION_SNOOZE,
        requestCode = SNOOZE_REQUEST_CODE,
        context = context
    )

    init {
        createNotificationChannels()
    }

    fun getAlarmNotification(label: String, time: String) =
        NotificationCompat.Builder(context, ALARM_WORKER_CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_alarm_24)
            .setShowWhen(true)
            .setContentTitle(R.string.app_name.toString())
            .setContentText("$label\n$time")
//            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
//            .setFullScreenIntent()
            .addAction(R.drawable.baseline_close_24, "Dismiss", dismissIntentAction)
            .addAction(R.drawable.baseline_snooze_24, "Snooze", snoozeIntentAction)
            .setOngoing(true)

    fun removeAlarmWorkerNotification() {
        notificationManager.cancel(ALARM_WORKER_NOTIFICATION_ID)
    }

    private fun createNotificationChannels() {
        val alarmWorkerChannel = NotificationChannelCompat.Builder(
            ALARM_WORKER_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_MAX
        )
            .setName("alarm worker channel")
            .setDescription("Shows alarm notification at scheduled time")
            .setSound(null, null)
            .build()

        notificationManager.createNotificationChannelsCompat(
            listOf(
                alarmWorkerChannel
            )
        )
    }
}

const val ALARM_WORKER_CHANNEL_ID = "alarm_worker_channel_id"
const val ALARM_WORKER_NOTIFICATION_ID = 15