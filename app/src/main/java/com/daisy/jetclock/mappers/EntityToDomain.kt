package com.daisy.jetclock.mappers

import com.daisy.jetclock.constants.MeridiemOption
import com.daisy.jetclock.constants.TimeFormat
import com.daisy.jetclock.localdata.entities.AlarmEntity
import com.daisy.jetclock.domain.Alarm

fun AlarmEntity.convertToDomain(timeFormat: TimeFormat = TimeFormat.Hour12Format): Alarm {
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
        minute = minute,
        meridiem = amPm,
        repeatDays = repeatDays,
        isEnabled = isEnabled,
        label = label,
        ringDuration = ringDuration,
        snoozeDuration = snoozeDuration,
        snoozeNumber = snoozeNumber,
        sound = sound
    )
}