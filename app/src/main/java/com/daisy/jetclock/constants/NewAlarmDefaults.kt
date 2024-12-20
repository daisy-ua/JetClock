package com.daisy.jetclock.constants

import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.model.TimeOfDay
import java.util.Calendar
import java.util.TimeZone

object NewAlarmDefaults {
    const val NEW_ALARM_ID: Long = 0
    const val DEFAULT_SOUND_ID: String = "Oversimplified.mp3"

    fun getNewAlarm() : Alarm = getLocalTime().let { time ->
        Alarm(
            id = NEW_ALARM_ID,
            hour = time.hour,
            minute = time.minute,
            meridiem = time.meridiem,
            repeatDays = listOf(),
            isEnabled = true,
            triggerTime = null,
            label = "Alarm",
            ringDuration = AlarmOptionsData.ringDurationOption[1],
            snoozeDuration = AlarmOptionsData.snoozeDuration[1],
            snoozeNumber = AlarmOptionsData.snoozeNumber[1],
            snoozeCount = 0,
            sound = DEFAULT_SOUND_ID
        )
    }

    private fun getLocalTime(): TimeOfDay {
        val calendar = Calendar.getInstance(TimeZone.getDefault())

        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)

        val meridiem = if (calendar.get(Calendar.AM_PM) == Calendar.AM) {
            MeridiemOption.AM
        } else {
            MeridiemOption.PM
        }

        return TimeOfDay(hour, minute, meridiem)
    }
}