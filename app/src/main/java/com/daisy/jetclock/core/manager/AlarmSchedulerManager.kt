package com.daisy.jetclock.core.manager

import com.daisy.jetclock.domain.Alarm

interface AlarmScheduler {
    fun schedule(alarm: Alarm)

    fun cancel(id: Long)
}