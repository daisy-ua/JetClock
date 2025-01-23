package com.daisy.jetclock.data.mapper

import com.daisy.jetclock.data.local.entity.AlarmEntity
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.model.MeridiemOption
import java.time.LocalTime

fun Alarm.convertToEntity(): AlarmEntity {
    val hour24 = with(time) {
        when {
            meridiem == MeridiemOption.PM && hour < 12 -> hour + 12
            meridiem == MeridiemOption.AM && hour == 12 -> 0
            else -> hour
        }
    }

    return AlarmEntity(
        id = id,
        time = LocalTime.of(hour24, time.minute),
        repeatDays = repeatDays.days,
        isEnabled = isEnabled,
        triggerTime = triggerTime,
        label = label,
        ringDuration = ringDuration,
        snoozeDuration = snoozeOption.duration,
        snoozeNumber = snoozeOption.number,
        snoozeCount = snoozeCount,
        sound = soundOption.soundFile
    )
}