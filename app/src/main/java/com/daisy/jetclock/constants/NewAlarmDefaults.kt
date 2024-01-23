package com.daisy.jetclock.constants

import com.daisy.jetclock.domain.Alarm

object NewAlarmDefaults {
    const val NEW_ALARM_ID: Long = 0
    const val DEFAULT_SOUND_ID: String = "Oversimplified.mp3"

    val NEW_ALARM: Alarm = Alarm(
        id = NEW_ALARM_ID,
        hour = 0,
        minute = 0,
        meridiem = null,
        repeatDays = listOf(),
        isEnabled = true,
        label = "Alarm",
        ringDuration = AlarmOptionsData.ringDurationOption[1],
        snoozeDuration = AlarmOptionsData.snoozeDuration[1],
        snoozeNumber = AlarmOptionsData.snoozeNumber[1],
        sound = DEFAULT_SOUND_ID
    )
}