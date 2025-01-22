package com.daisy.jetclock.data.mapper

import com.daisy.jetclock.data.local.entity.AlarmEntity
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.model.MeridiemOption
import com.daisy.jetclock.domain.model.RepeatDays
import com.daisy.jetclock.domain.model.RingDurationOption
import com.daisy.jetclock.domain.model.SnoozeOption
import com.daisy.jetclock.domain.model.SoundOption
import com.daisy.jetclock.domain.model.TimeFormat
import com.daisy.jetclock.domain.model.TimeOfDay

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
        time = TimeOfDay(hour, time.minute, amPm),
        repeatDays = RepeatDays(repeatDays),
        isEnabled = isEnabled,
        triggerTime = triggerTime,
        label = label,
        ringDurationOption = RingDurationOption(ringDuration),
        snoozeOption = SnoozeOption(snoozeDuration, snoozeNumber),
        snoozeCount = snoozeCount,
        soundOption = SoundOption(sound)
    )
}