package com.daisy.jetclock.utils

import com.daisy.jetclock.domain.model.Alarm

interface AlarmDataCallback {
    fun onAlarmUpdated(alarm: Alarm)
}