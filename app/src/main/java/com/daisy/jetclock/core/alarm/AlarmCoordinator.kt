package com.daisy.jetclock.core.alarm

import android.content.Context
import com.daisy.jetclock.core.manager.AlarmController
import com.daisy.jetclock.core.notification.AlarmNotificationManager
import com.daisy.jetclock.core.notification.AlarmNotificationType
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.model.SoundOption
import com.daisy.jetclock.presentation.utils.formatter.TimeFormatter
import com.daisy.jetclock.utils.MediaPlayerManager
import com.daisy.jetclock.utils.scope.CoroutineScopeProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlarmCoordinator @Inject constructor(
    @ApplicationContext private val context: Context,
    coroutineScopeProvider: CoroutineScopeProvider,
    private val mediaPlayerManager: MediaPlayerManager,
    private val notificationManager: AlarmNotificationManager,
    private val alarmController: AlarmController,
) : AlarmAction {

    private val scope = coroutineScopeProvider.getCoroutineScope()

    override suspend fun start(
        alarm: Alarm,
        timestamp: String,
        onAutoSnoozeComplete: (Boolean) -> Unit,
    ): AlarmNotificationType.Ongoing {
        notificationManager.hideNotification(
            AlarmNotificationType.Snoozed(alarm.id)
        )

        startMediaPlayback(alarm.soundOption)

        val displayTimestamp =
            timestamp.ifEmpty { TimeFormatter.formatTimeWithMeridiem(context, alarm.time) }

        scheduleAutoSnooze(alarm) {
            onAutoSnoozeComplete(true)
        }

        return AlarmNotificationType.Ongoing(alarm.id, alarm.label, displayTimestamp)
    }

    override suspend fun snooze(alarm: Alarm) {
        stopMediaPlayback()

        val updatedAlarm = alarmController.snooze(alarm)

        notificationManager.showNotification(
            AlarmNotificationType.Snoozed(
                updatedAlarm.id,
                updatedAlarm.label,
                TimeFormatter.formatTimeWithMeridiem(context, updatedAlarm.time)
            )
        )

        resetSnoozeCountIfNeeded(alarm)
    }

    override suspend fun dismiss(alarm: Alarm) {
        stopMediaPlayback()
        if (alarm.repeatDays.days.isNotEmpty()) {
            alarmController.reschedule(alarm)
        } else {
            alarmController.cancel(alarm)
        }
    }

    fun cleanup() {
        scope.cancel()
        stopMediaPlayback()
    }

    private fun scheduleAutoSnooze(alarm: Alarm, onComplete: () -> Unit) {
        scope.launch {
            delay(alarm.ringDurationOption.value * 60 * 1000L)
            autoSnoozeAlarm(alarm)
            onComplete()
        }
    }

    private suspend fun autoSnoozeAlarm(alarm: Alarm) {
        val notification = if (alarm.snoozeCount < alarm.snoozeOption.number) {
            alarmController.autoSnooze(alarm)

            AlarmNotificationType.Snoozed(
                alarm.id,
                alarm.label,
                TimeFormatter.formatTimeWithMeridiem(context, alarm.time)
            )
        } else {
            dismiss(alarm)

            AlarmNotificationType.Missed(
                alarm.id,
                alarm.label,
                TimeFormatter.formatTimeWithMeridiem(context, alarm.time)
            )
        }

        notificationManager.showNotification(notification)
    }

    private suspend fun resetSnoozeCountIfNeeded(alarm: Alarm) {
        if (alarm.snoozeCount > 0) {
            alarmController.resetAlarmSnoozeCount(alarm)
        }
    }

    //    TODO: move to ServicePlayback
    private fun startMediaPlayback(sound: SoundOption) {
        mediaPlayerManager.prepare(sound, true)
        mediaPlayerManager.start()
    }

    private fun stopMediaPlayback() {
        mediaPlayerManager.release()
    }
}