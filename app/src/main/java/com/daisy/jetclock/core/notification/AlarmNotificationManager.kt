package com.daisy.jetclock.core.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.daisy.jetclock.R
import com.daisy.jetclock.app.MainActivity
import com.daisy.jetclock.constants.ConfigConstants
import com.daisy.jetclock.core.utils.IntentExtra
import com.daisy.jetclock.core.base.BaseNotificationManager
import com.daisy.jetclock.core.base.NotificationChannelData
import com.daisy.jetclock.core.receiver.AlarmBroadcastReceiver
import com.daisy.jetclock.core.utils.setIntentAction
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmNotificationManager @Inject constructor(
    @ApplicationContext val context: Context,
) : BaseNotificationManager<AlarmNotificationType>(NotificationManagerCompat.from(context)) {

    private val alarmBroadcastReceiver = AlarmBroadcastReceiver::class.java

    private val channelData: List<NotificationChannelData>
        get() = listOf(
            NotificationChannelData(
                channelId = NotificationConfig.ALARM_ONGOING_CHANNEL_ID,
                importance = NotificationManagerCompat.IMPORTANCE_MAX,
                name = context.getString(R.string.alarm_upcoming_channel_name),
                description = context.getString(R.string.alarm_upcoming_channel_description),
            ),

            NotificationChannelData(
                channelId = NotificationConfig.ALARM_MISSED_CHANNEL_ID,
                importance = NotificationManagerCompat.IMPORTANCE_HIGH,
                name = context.getString(R.string.alarm_missed_channel_name),
                description = context.getString(R.string.alarm_missed_channel_description),
                soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ),
            NotificationChannelData(
                channelId = NotificationConfig.ALARM_SNOOZED_CHANNEL_ID,
                importance = NotificationManagerCompat.IMPORTANCE_LOW,
                name = context.getString(R.string.alarm_snooze_channel_name),
                description = context.getString(R.string.alarm_snooze_channel_description),
            )
        )

    init {
        createNotificationChannels(channelData)
    }

    override fun createNotification(type: AlarmNotificationType): Notification {
        return when (type) {
            is AlarmNotificationType.Ongoing -> with(type) {
                getOngoingNotification(id, label, time)
            }

            is AlarmNotificationType.Missed -> with(type) {
                getMissedNotification(label, time)
            }

            is AlarmNotificationType.Snoozed -> with(type) {
                getSnoozedNotification(label, time)
            }
        }
    }

    private fun getOngoingNotification(alarmId: Long, label: String?, time: String?): Notification {
        return NotificationCompat.Builder(context, NotificationConfig.ALARM_ONGOING_CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_alarm_24)
            .setShowWhen(true)
            .setContentTitle(label)
            .setContentText(time)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(getFullScreenIntent(), true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                R.drawable.baseline_close_24,
                context.getString(R.string.dismiss_action_text),
                getDismissIntentAction(alarmId)
            )
            .addAction(
                R.drawable.baseline_snooze_24,
                context.getString(R.string.snooze_action_text),
                getSnoozeIntentAction(alarmId)
            )
            .setOngoing(true)
            .build()
    }

    private fun getMissedNotification(label: String?, time: String?): Notification {
        return NotificationCompat.Builder(context, NotificationConfig.ALARM_MISSED_CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_alarm_24)
            .setContentTitle(context.getString(R.string.missed_alarm_title, label))
            .setContentText(context.getString(R.string.missed_alarm_content, time))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(getFullScreenIntent())
            .build()
    }

    private fun getSnoozedNotification(label: String?, time: String?): Notification {
        return NotificationCompat.Builder(context, NotificationConfig.ALARM_SNOOZED_CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_alarm_24)
            .setContentTitle(context.getString(R.string.snoozed_alarm_title, label))
            .setContentText(context.getString(R.string.snoozed_alarm_content, time))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(getFullScreenIntent())
            .build()
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

    private fun getFullScreenIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(context, 0, intent, ConfigConstants.PENDING_INTENT_FLAGS)
    }
}

const val DISMISS_REQUEST_CODE = 1001
const val SNOOZE_REQUEST_CODE = 1002