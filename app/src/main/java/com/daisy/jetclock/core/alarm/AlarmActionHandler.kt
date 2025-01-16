package com.daisy.jetclock.core.alarm

import android.content.Context
import android.content.Intent
import com.daisy.jetclock.core.notification.AlarmNotificationManager
import com.daisy.jetclock.core.notification.AlarmNotificationType
import com.daisy.jetclock.core.scheduler.AlarmSchedulerManager
import com.daisy.jetclock.core.service.AlarmMediaService
import com.daisy.jetclock.core.utils.AlarmStateUpdater
import com.daisy.jetclock.core.utils.IntentExtra
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.usecase.GetAlarmDetailsUseCase
import com.daisy.jetclock.presentation.utils.formatter.TimeFormatter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlarmActionHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getAlarmDetailsUseCase: GetAlarmDetailsUseCase,
    private val notificationManager: AlarmNotificationManager,
    private val alarmSchedulerManager: AlarmSchedulerManager,
    private val alarmStateUpdater: AlarmStateUpdater,
) : AlarmAction {

    private val autoSnoozeJobs = mutableMapOf<Long, Job>()

    override suspend fun start(
        alarmId: Long,
    ) {
        notificationManager.hideNotification(
            AlarmNotificationType.Snoozed(alarmId)
        )
        val alarm = getAlarmDetailsUseCase(alarmId).firstOrNull() ?: return

        scheduleAutoSnooze(alarm)
//        TODO: change to Alarm
        startOngoingAlarmService(alarmId)
    }

    override suspend fun snooze(id: Long) {
        cancelAutoSnoozeJob(id)

        val alarm = getAlarmDetailsUseCase(id).firstOrNull() ?: return

        stopOngoingAlarmServiceIfNeeded(id)

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

    override suspend fun dismiss(id: Long) {
        cancelAutoSnoozeJob(id)

        disableOngoingAlarm(id)
    }

    override suspend fun cancel(id: Long) {
        stopOngoingAlarmServiceIfNeeded(id)
    }

    private suspend fun disableOngoingAlarm(id: Long) {
        val alarm = getAlarmDetailsUseCase(id).firstOrNull() ?: return

        stopOngoingAlarmServiceIfNeeded(id)

        if (alarm.repeatDays.days.isNotEmpty()) {
            val timeInMillis = alarmSchedulerManager.schedule(alarm)
            alarmStateUpdater.rescheduleAlarm(alarm, timeInMillis)
        } else {
            alarmStateUpdater.cancelAlarm(alarm)
        }
    }

    private fun scheduleAutoSnooze(alarm: Alarm) {
        cancelAutoSnoozeJob(alarm.id)

        val job = CoroutineScope(Job() + Dispatchers.Main).launch {
            try {
                delay(alarm.ringDurationOption.value * 60 * 1000L)

                stopOngoingAlarmServiceIfNeeded(alarm.id)

                autoSnoozeAlarm(alarm)
            } finally {
                autoSnoozeJobs.remove(alarm.id)
            }
        }
        autoSnoozeJobs[alarm.id] = job
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
            disableOngoingAlarm(alarm.id)

            AlarmNotificationType.Missed(
                alarm.id,
                alarm.label,
                TimeFormatter.formatTimeWithMeridiem(context, alarm.time)
            )
        }

        notificationManager.showNotification(notification)
    }

    private fun cancelAutoSnoozeJob(id: Long) {
        autoSnoozeJobs[id]?.cancel()
    }

    private fun startOngoingAlarmService(id: Long) {
        val serviceIntent =
            Intent(context, AlarmMediaService::class.java).apply {
                putExtra(IntentExtra.ID_EXTRA, id)

                action = AlarmMediaService.ACTION_START
            }

        context.startForegroundService(serviceIntent)
    }

    private fun stopOngoingAlarmServiceIfNeeded(id: Long) {
        val serviceIntent =
            Intent(context, AlarmMediaService::class.java).apply {
                putExtra(IntentExtra.ID_EXTRA, id)

                action = AlarmMediaService.ACTION_STOP
            }

        context.startService(serviceIntent)
    }
}