package com.daisy.jetclock.constants

import android.content.Context
import com.daisy.jetclock.R
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.model.MeridiemOption
import com.daisy.jetclock.domain.model.RepeatDays
import com.daisy.jetclock.domain.model.SnoozeOption
import com.daisy.jetclock.domain.model.SoundOption
import com.daisy.jetclock.domain.model.TimeOfDay
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAlarmConfig @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    val defaultAlarm: Alarm
        get() = Alarm(
            id = NEW_ALARM_ID,
            time = with(getLocalTime()) {
                TimeOfDay(
                    hour,
                    minute,
                    meridiem
                )
            },
            repeatDays = RepeatDays(listOf()),
            isEnabled = true,
            triggerTime = null,
            label = context.getString(R.string.default_label),
            ringDuration = AlarmOptionsData.ringDurationOption[1],
            snoozeOption = SnoozeOption(
                duration = AlarmOptionsData.snoozeDuration[1],
                number = AlarmOptionsData.snoozeNumber[1],
            ),
            snoozeCount = 0,
            soundOption = SoundOption.default
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

    companion object {
        const val NEW_ALARM_ID: Long = 0
        const val DEFAULT_SOUND_ID: String = "Oversimplified.mp3"
    }
}