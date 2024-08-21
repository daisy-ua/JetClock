package com.daisy.jetclock.core.manager

import com.daisy.jetclock.domain.Alarm
import com.daisy.jetclock.utils.AlarmDataCallback

interface AlarmSchedulerManager {
    fun schedule(alarm: Alarm): String

    fun reschedule(alarm: Alarm): Long

    fun snooze(alarm: Alarm): Alarm

    fun cancel(alarm: Alarm)

    fun disable(alarm: Alarm)

    fun getNextAlarmTime(alarms: List<Alarm>): Pair<Alarm, String>?

    fun setAlarmDataCallback(callback: AlarmDataCallback?): Unit
}