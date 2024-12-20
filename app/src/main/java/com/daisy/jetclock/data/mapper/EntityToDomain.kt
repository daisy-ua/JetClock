package com.daisy.jetclock.data.mapper

import com.daisy.jetclock.constants.MeridiemOption
import com.daisy.jetclock.constants.TimeFormat
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.data.local.entity.AlarmEntity

fun AlarmEntity.convertToDomain(timeFormat: TimeFormat = TimeFormat.Hour12Format): Alarm {
    val hour24 = time.hour

    var amPm: MeridiemOption? = null
    val hour = if (timeFormat == TimeFormat.Hour12Format) {
        amPm = if (hour24 >= 12) MeridiemOption.PM else MeridiemOption.AM
        if (hour24 % 12 == 0) 12 else hour24 % 12
    } else {
        hour24
    }

    return Alarm(
        id = id,
        hour = hour,
        minute = time.minute,
        meridiem = amPm,
        repeatDays = repeatDays,
        isEnabled = isEnabled,
        triggerTime = triggerTime,
        label = label,
        ringDuration = ringDuration,
        snoozeDuration = snoozeDuration,
        snoozeNumber = snoozeNumber,
        snoozeCount = snoozeCount,
        sound = sound
    )
}