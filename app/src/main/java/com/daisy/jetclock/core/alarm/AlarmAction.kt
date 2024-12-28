package com.daisy.jetclock.core.alarm

import com.daisy.jetclock.core.notification.AlarmNotificationType
import com.daisy.jetclock.domain.model.Alarm

interface AlarmAction {

    suspend fun start(
        alarm: Alarm,
        timestamp: String,
        onAutoSnoozeComplete: (Boolean) -> Unit,
    ): AlarmNotificationType.Ongoing

    suspend fun snooze(alarm: Alarm)

    suspend fun dismiss(alarm: Alarm)
}