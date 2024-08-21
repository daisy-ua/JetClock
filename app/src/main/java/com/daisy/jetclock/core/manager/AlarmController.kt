package com.daisy.jetclock.core.manager

import com.daisy.jetclock.domain.Alarm
import com.daisy.jetclock.utils.AlarmDataCallback
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmController @Inject constructor(
    private val alarmSchedulerManager: AlarmSchedulerManager,
) {
    private var dataCallback: AlarmDataCallback? = null

    fun setAlarmDataCallback(callback: AlarmDataCallback?) {
        this.dataCallback = callback
    }

    fun schedule(alarm: Alarm): Long {
        val timeInMillis = alarmSchedulerManager.schedule(alarm)
        updateAlarm(alarm, timeInMillis)
        return timeInMillis
    }

    fun snooze(alarm: Alarm): Alarm {
        val updatedAlarm = alarmSchedulerManager.snooze(alarm)
        updateAlarm(updatedAlarm)
        return updatedAlarm
    }

    fun cancel(alarm: Alarm) {
        updateAlarm(alarm, null)
        alarmSchedulerManager.cancel(alarm)
    }

    fun delete(alarm: Alarm) {
        if (alarm.isEnabled) {
            alarmSchedulerManager.cancel(alarm)
        }
    }

    fun updateAlarm(alarm: Alarm) {
        dataCallback?.onAlarmUpdated(alarm)
    }

    private fun updateAlarm(alarm: Alarm, timeInMillis: Long?) {
        dataCallback?.onAlarmUpdated(alarm.apply { triggerTime = timeInMillis })
    }
}
