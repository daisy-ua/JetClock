package com.daisy.jetclock.domain.model

data class TimeOfDay(
    val hour: Int,
    val minute: Int,
    val meridiem: MeridiemOption?,
) {
    val timeFormat: TimeFormat
        get() = meridiem?.run { TimeFormat.Hour12Format } ?: TimeFormat.Hour24Format

    fun format(timeFormat: TimeFormat): TimeOfDay {
        return when (timeFormat) {
            TimeFormat.Hour12Format -> to12HourFormat()

            TimeFormat.Hour24Format -> to24HourFormat()
        }
    }

    fun to12HourFormat(): TimeOfDay {
        if (meridiem != null) return this

        val adjustedHours = when {
            hour == 0 -> 12

            hour > 12 -> hour - 12

            else -> hour
        }

        val adjustedMeridiem = if (hour < 12) MeridiemOption.AM else MeridiemOption.PM

        return TimeOfDay(adjustedHours, minute, adjustedMeridiem)
    }

    fun to24HourFormat(): TimeOfDay {
        if (meridiem == null) return this

        val adjustedHours = when {
            meridiem == MeridiemOption.PM && hour != 12 -> hour + 12

            meridiem == MeridiemOption.AM && hour == 12 -> 0

            else -> hour
        }

        return TimeOfDay(adjustedHours, minute, null)
    }
}