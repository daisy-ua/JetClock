package com.daisy.jetclock.core.manager

import com.daisy.jetclock.domain.Alarm

interface AlarmSchedulerManager {
    fun schedule(alarm: Alarm)

    fun snooze(alarm: Alarm, minutes: Int)

    fun cancel(id: Long)
}