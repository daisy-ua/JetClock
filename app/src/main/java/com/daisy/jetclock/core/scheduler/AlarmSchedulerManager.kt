package com.daisy.jetclock.core.scheduler

import com.daisy.jetclock.domain.model.Alarm

interface AlarmSchedulerManager {
    fun schedule(alarm: Alarm): Long

    fun snooze(alarm: Alarm): Long

    fun cancel(alarm: Alarm)
}