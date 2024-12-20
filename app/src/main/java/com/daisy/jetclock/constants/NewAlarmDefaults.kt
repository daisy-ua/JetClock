package com.daisy.jetclock.constants

import android.content.Context
import com.daisy.jetclock.R
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.model.RepeatDays
import com.daisy.jetclock.domain.model.RingDurationOption
import com.daisy.jetclock.domain.model.SnoozeOption
import com.daisy.jetclock.domain.model.SoundOption
import com.daisy.jetclock.domain.model.TimeOfDay
import java.util.Calendar
import java.util.TimeZone

object NewAlarmDefaults {
    const val NEW_ALARM_ID: Long = 0
    const val DEFAULT_SOUND_ID: String = "Oversimplified.mp3"

    fun getNewAlarm(context: Context): Alarm = Alarm(
        id = NEW_ALARM_ID,
        time = with(getLocalTime()) { TimeOfDay(hour, minute, meridiem) },
        repeatDays = RepeatDays(listOf()),
        isEnabled = true,
        triggerTime = null,
        label = context.getString(R.string.default_label),
        ringDurationOption = RingDurationOption(AlarmOptionsData.ringDurationOption[1]),
        snoozeOption = SnoozeOption(
            duration = AlarmOptionsData.snoozeDuration[1],
            number = AlarmOptionsData.snoozeNumber[1],
        ),
        snoozeCount = 0,
        soundOption = SoundOption(DEFAULT_SOUND_ID)
    )

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