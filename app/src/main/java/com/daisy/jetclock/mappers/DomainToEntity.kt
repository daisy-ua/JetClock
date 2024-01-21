package com.daisy.jetclock.mappers

import com.daisy.jetclock.constants.MeridiemOption
import com.daisy.jetclock.localdata.entities.AlarmEntity
import com.daisy.jetclock.domain.Alarm

fun Alarm.convertToEntity(): AlarmEntity {
    val hour24 = when {
        meridiem == MeridiemOption.PM && hour < 12 -> hour + 12
        meridiem == MeridiemOption.AM && hour == 12 -> 0
        else -> hour
    }

    return AlarmEntity(
        id = id,
        hour24 = hour24,
        minute = minute,
        repeatDays = repeatDays,
        isEnabled = isEnabled,
        label = label,
        ringDuration = ringDuration,
        snoozeDuration = snoozeDuration,
        snoozeNumber = snoozeNumber,
        sound = sound
    )
}