package com.daisy.jetclock.utils

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.daisy.jetclock.MainActivity
import com.daisy.jetclock.R
import com.daisy.jetclock.constants.ConfigConstants
import com.daisy.jetclock.core.IntentExtra
import com.daisy.jetclock.core.NotificationConfig
import com.daisy.jetclock.core.receiver.AlarmBroadcastReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val notificationManager = NotificationManagerCompat.from(context)

    private val alarmBroadcastReceiver = AlarmBroadcastReceiver::class.java

    init {
        createNotificationChannels()
    }

    fun getAlarmNotification(alarmId: Long, label: String, time: String) =
        NotificationCompat.Builder(context, NotificationConfig.ALARM_UPCOMING_CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_alarm_24)
            .setShowWhen(true)
            .setContentTitle(label)
            .setContentText(time)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(getFullScreenIntent(), true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.baseline_close_24, "Dismiss", getDismissIntentAction(alarmId))
            .addAction(R.drawable.baseline_snooze_24, "Snooze", getSnoozeIntentAction(alarmId))
            .setOngoing(true)
            .build()

    @SuppressLint("MissingPermission")
    fun showAlarmMissedNotification(label: String, time: String) {
        val alarmMissedNotification =
            NotificationCompat.Builder(context, NotificationConfig.ALARM_MISSED_CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_alarm_24)
                .setContentTitle("$label (Missed)")
                .setContentText("Missed at $time")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(getFullScreenIntent())
                .build()

        notificationManager.notify(
            NotificationConfig.ALARM_MISSED_NOTIFICATION_ID,
            alarmMissedNotification
        )
    }

    @SuppressLint("MissingPermission")
    fun showAlarmSnoozedNotification(label: String, time: String) {
        val alarmSnoozedNotification =
            NotificationCompat.Builder(context, NotificationConfig.ALARM_SNOOZE_CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_alarm_24)
                .setContentTitle("$label (Snoozed)")
                .setContentText("Snoozing until $time")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(getFullScreenIntent())
                .build()

        notificationManager.notify(
            NotificationConfig.ALARM_SNOOZE_NOTIFICATION_ID,
            alarmSnoozedNotification
        )
    }

    fun removeAlarmUpcomingNotification() {
        notificationManager.cancel(NotificationConfig.ALARM_UPCOMING_NOTIFICATION_ID)
    }

    fun removeAlarmSnoozedNotification() {
        notificationManager.cancel(NotificationConfig.ALARM_SNOOZE_NOTIFICATION_ID)
    }

    private fun getDismissIntentAction(alarmId: Long) = alarmBroadcastReceiver.setIntentAction(
        actionName = AlarmBroadcastReceiver.ACTION_DISMISS,
        requestCode = DISMISS_REQUEST_CODE,
        extras = mapOf(IntentExtra.ID_EXTRA to alarmId),
        context = context
    )

    private fun getSnoozeIntentAction(alarmId: Long) = alarmBroadcastReceiver.setIntentAction(
        actionName = AlarmBroadcastReceiver.ACTION_SNOOZE,
        requestCode = SNOOZE_REQUEST_CODE,
        extras = mapOf(IntentExtra.ID_EXTRA to alarmId),
        context = context
    )

    private fun createNotificationChannels() {
        val alarmUpcomingChannel = NotificationChannelCompat.Builder(
            NotificationConfig.ALARM_UPCOMING_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_MAX
        )
            .setName("Upcoming alarms")
            .setDescription("Shows alarm notification at scheduled time")
            .setSound(null, null)
            .build()

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val alarmMissedChannel = NotificationChannelCompat.Builder(
            NotificationConfig.ALARM_MISSED_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_HIGH
        )
            .setName("Missed alarms")
            .setDescription("Shows notification for missed alarms")
            .setSound(soundUri, null)
            .build()

        val alarmSnoozedChannel = NotificationChannelCompat.Builder(
            NotificationConfig.ALARM_SNOOZE_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_LOW
        )
            .setName("Snoozed alarms")
            .setDescription("Shows notification for snoozed alarms")
            .setSound(null, null)
            .build()

        notificationManager.createNotificationChannelsCompat(
            listOf(
                alarmUpcomingChannel,
                alarmMissedChannel,
                alarmSnoozedChannel
            )
        )
    }

    private fun getFullScreenIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(context, 0, intent, ConfigConstants.PENDING_INTENT_FLAGS)
    }
}

const val DISMISS_REQUEST_CODE = 1001
const val SNOOZE_REQUEST_CODE = 1002