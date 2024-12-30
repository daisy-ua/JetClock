package com.daisy.jetclock.core.utils

import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.repository.AlarmRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmStateUpdater @Inject constructor(
    private val alarmRepository: AlarmRepository,
) {
    suspend fun scheduleAlarm(alarm: Alarm, timeInMillis: Long) {
        alarm.copy(
            triggerTime = timeInMillis,
            isEnabled = true
        ).apply { update() }
    }

    suspend fun rescheduleAlarm(alarm: Alarm, timeInMillis: Long) {
        alarm.copy(
            triggerTime = timeInMillis,
            isEnabled = true,
            snoozeCount = 0
        ).apply { update() }
    }

    suspend fun snoozeAlarm(alarm: Alarm, timeInMillis: Long) {
        alarm.copy(
            triggerTime = timeInMillis,
            snoozeCount = 0
        ).apply { update() }
    }

    suspend fun autoSnoozeAlarm(alarm: Alarm, timeInMillis: Long) {
        alarm.copy(
            triggerTime = timeInMillis,
            snoozeCount = alarm.snoozeCount + 1
        ).apply { update() }
    }

    suspend fun cancelAlarm(alarm: Alarm) {
        alarm.copy(
            triggerTime = null,
            snoozeCount = 0,
            isEnabled = false
        ).apply { update() }
    }

    private suspend fun Alarm.update() {
        alarmRepository.insertAlarm(this)
    }
}