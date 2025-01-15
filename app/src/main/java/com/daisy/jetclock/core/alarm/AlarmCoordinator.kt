package com.daisy.jetclock.core.alarm

import android.content.Context
import com.daisy.jetclock.core.media.PlaybackService
import com.daisy.jetclock.core.notification.AlarmNotificationManager
import com.daisy.jetclock.core.notification.AlarmNotificationType
import com.daisy.jetclock.core.notification.fullscreen.FullscreenNotificationStopper
import com.daisy.jetclock.core.scheduler.AlarmSchedulerManager
import com.daisy.jetclock.core.utils.AlarmStateUpdater
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.presentation.utils.formatter.TimeFormatter
import com.daisy.jetclock.utils.scope.CoroutineScopeProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlarmCoordinator @Inject constructor(
    @ApplicationContext private val context: Context,
    coroutineScopeProvider: CoroutineScopeProvider,
    private val mediaPlaybackService: PlaybackService,
    private val notificationManager: AlarmNotificationManager,
    private val alarmSchedulerManager: AlarmSchedulerManager,
    private val alarmStateUpdater: AlarmStateUpdater,
    private val fullscreenNotificationStopper: FullscreenNotificationStopper,
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

        mediaPlaybackService.startPlayback(alarm.soundOption)

        val displayTimestamp =
            timestamp.ifEmpty { TimeFormatter.formatTimeWithMeridiem(context, alarm.time) }

        scheduleAutoSnooze(alarm) {
            fullscreenNotificationStopper.stopNotification()
            onAutoSnoozeComplete(true)
        }

        return AlarmNotificationType.Ongoing(alarm.id, alarm.label, displayTimestamp)
    }

    override suspend fun snooze(alarm: Alarm) {
        mediaPlaybackService.stopPlayback()

        fullscreenNotificationStopper.stopNotification()

        val timeInMillis = alarmSchedulerManager.snooze(alarm)
        alarmStateUpdater.snoozeAlarm(alarm, timeInMillis)

        notificationManager.showNotification(
            AlarmNotificationType.Snoozed(
                alarm.id,
                alarm.label,
                TimeFormatter.formatTimeWithMeridiem(context, timeInMillis)
            )
        )
    }

    override suspend fun dismiss(alarm: Alarm) {
        mediaPlaybackService.stopPlayback()

        fullscreenNotificationStopper.stopNotification()

        if (alarm.repeatDays.days.isNotEmpty()) {
            val timeInMillis = alarmSchedulerManager.schedule(alarm)
            alarmStateUpdater.rescheduleAlarm(alarm, timeInMillis)
        } else {
            alarmStateUpdater.cancelAlarm(alarm)
        }
    }

    fun cleanup() {
        scope.cancel()
        mediaPlaybackService.stopPlayback()
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
            val timeInMillis = alarmSchedulerManager.snooze(alarm)
            alarmStateUpdater.autoSnoozeAlarm(alarm, timeInMillis)

            AlarmNotificationType.Snoozed(
                alarm.id,
                alarm.label,
                TimeFormatter.formatTimeWithMeridiem(context, timeInMillis)
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
}