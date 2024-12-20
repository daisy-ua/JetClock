package com.daisy.jetclock.core.manager

import com.daisy.jetclock.domain.model.Alarm

interface AlarmSchedulerManager {
    fun schedule(alarm: Alarm): Long

    fun snooze(alarm: Alarm): Alarm

    fun cancel(alarm: Alarm)
}