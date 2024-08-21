package com.daisy.jetclock.utils

import com.daisy.jetclock.domain.Alarm

interface AlarmDataCallback {
    fun onAlarmUpdated(alarm: Alarm)
}