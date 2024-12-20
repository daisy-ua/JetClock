package com.daisy.jetclock.core.manager

import com.daisy.jetclock.constants.NewAlarmDefaults
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.repository.AlarmRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmController @Inject constructor(
    private val alarmSchedulerManager: AlarmSchedulerManager,
    private val alarmRepository: AlarmRepository,
) {
    suspend fun schedule(alarm: Alarm): Long {
        val timeInMillis = alarmSchedulerManager.schedule(alarm)
        val updatedAlarm = alarm.copy(triggerTime = timeInMillis, isEnabled = true)
        updateAlarm(updatedAlarm)
        return timeInMillis
    }

    suspend fun reschedule(alarm: Alarm): Long {
        return schedule(alarm.copy(snoozeCount = 0))
    }

    suspend fun resetAlarmSnoozeCount(alarm: Alarm) {
        updateAlarm(alarm.copy(snoozeCount = 0))
    }

    suspend fun snooze(alarm: Alarm): Alarm {
        val updatedAlarm = alarmSchedulerManager.snooze(alarm)
        updateAlarm(updatedAlarm)
        return updatedAlarm
    }

    suspend fun autoSnooze(alarm: Alarm): Alarm {
        return snooze(alarm.copy(snoozeCount = alarm.snoozeCount + 1))
    }

    suspend fun cancel(alarm: Alarm) {
        updateAlarm(alarm.copy(triggerTime = null, snoozeCount = 0, isEnabled = false))
        alarmSchedulerManager.cancel(alarm)
    }

    suspend fun delete(alarm: Alarm) {
        alarmRepository.deleteAlarm(alarm.id)
        if (alarm.isEnabled) {
            alarmSchedulerManager.cancel(alarm)
        }
    }

    suspend fun insert(alarm: Alarm): Alarm {
        val newId = alarmRepository.insertAlarm(alarm)
        if (alarm.id == NewAlarmDefaults.NEW_ALARM_ID) {
            return alarm.copy(id = newId)
        }
        return alarm
    }

    private suspend fun updateAlarm(alarm: Alarm) {
        alarmRepository.insertAlarm(alarm)
    }
}
