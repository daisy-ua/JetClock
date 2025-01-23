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
import com.daisy.jetclock.domain.model.TimeFormat
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
    private var activeAlarmId: Long? = null

    override suspend fun start(
        alarmId: Long,
    ) {
        notificationManager.hideNotification(
            AlarmNotificationType.Snoozed(alarmId)
        )
        val alarm = getAlarmDetailsUseCase(alarmId).firstOrNull() ?: return

        if (isAlarmTimeInThePast(alarm.triggerTime!!)) return

        if (activeAlarmId != null) {
            updateAlarmSchedule(alarmId)
            return
        }

        scheduleAutoSnooze(alarm)
        startOngoingAlarmService(alarm)
    }

    override suspend fun snooze(id: Long) {
        cancelAutoSnoozeJob(id)

        stopOngoingAlarmServiceIfNeeded(id)

        val alarm = getAlarmDetailsUseCase(id).firstOrNull() ?: return

        val timeInMillis = alarmSchedulerManager.snooze(alarm)
        alarmStateUpdater.snoozeAlarm(alarm, timeInMillis)

//        TODO: remove hardcoded
        notificationManager.showNotification(
            AlarmNotificationType.Snoozed(
                alarm.id,
                alarm.label,
                TimeFormatter.formatTimeWithMeridiem(context, timeInMillis, TimeFormat.Hour24Format)
            )
        )
    }

    override suspend fun dismiss(id: Long) {
        cancelAutoSnoozeJob(id)

        stopOngoingAlarmServiceIfNeeded(id)

        updateAlarmSchedule(id)
    }

    override suspend fun cancel(id: Long) {
        stopOngoingAlarmServiceIfNeeded(id)
    }

    private fun isAlarmTimeInThePast(triggerTime: Long, bufferTimeMillis: Long = 5000L): Boolean {
        return triggerTime + bufferTimeMillis < System.currentTimeMillis()
    }

    private fun scheduleAutoSnooze(alarm: Alarm) {
        cancelAutoSnoozeJob(alarm.id)

        val job = CoroutineScope(Job() + Dispatchers.Default).launch {
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

//            TODO: remove hardcoded
            AlarmNotificationType.Snoozed(
                alarm.id,
                alarm.label,
                TimeFormatter.formatTimeWithMeridiem(context, timeInMillis, TimeFormat.Hour24Format)
            )
        } else {
            updateAlarmSchedule(alarm.id)

            AlarmNotificationType.Missed(
                alarm.id,
                alarm.label,
                TimeFormatter.formatTimeWithMeridiem(context, alarm.time)
            )
        }

        notificationManager.showNotification(notification)
    }

    private suspend fun updateAlarmSchedule(id: Long) {
        val alarm = getAlarmDetailsUseCase(id).firstOrNull() ?: return

        if (alarm.repeatDays.days.isNotEmpty()) {
            val timeInMillis = alarmSchedulerManager.schedule(alarm)
            alarmStateUpdater.rescheduleAlarm(alarm, timeInMillis)
        } else {
            alarmStateUpdater.cancelAlarm(alarm)
        }
    }

    private fun cancelAutoSnoozeJob(id: Long) {
        autoSnoozeJobs[id]?.cancel()
    }

    private fun startOngoingAlarmService(alarm: Alarm) {
        val serviceIntent =
            Intent(context, AlarmMediaService::class.java).apply {
                putExtra(IntentExtra.DATA_EXTRA, alarm)

                action = AlarmMediaService.ACTION_START
            }

        context.startForegroundService(serviceIntent)

        activeAlarmId = alarm.id
    }

    private fun stopOngoingAlarmServiceIfNeeded(id: Long) {
        val serviceIntent =
            Intent(context, AlarmMediaService::class.java).apply {
                putExtra(IntentExtra.ID_EXTRA, id)

                action = AlarmMediaService.ACTION_STOP
            }

        context.startService(serviceIntent)

        activeAlarmId = null
    }
}