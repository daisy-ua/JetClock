package com.daisy.jetclock.data.mapper

import com.daisy.jetclock.constants.MeridiemOption
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.data.local.entity.AlarmEntity
import java.time.LocalTime

fun Alarm.convertToEntity(): AlarmEntity {
    val hour24 = when {
        meridiem == MeridiemOption.PM && hour < 12 -> hour + 12
        meridiem == MeridiemOption.AM && hour == 12 -> 0
        else -> hour
    }

    return AlarmEntity(
        id = id,
        time = LocalTime.of(hour24, minute),
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