package com.daisy.jetclock.core.utils

import com.daisy.jetclock.domain.model.MeridiemOption
import com.daisy.jetclock.domain.model.DayOfWeek
import com.daisy.jetclock.domain.model.TimeOfDay
import java.util.Calendar
import javax.inject.Singleton

/**
 * This utility object provides helper functions for working with time-related calculations.
 * It includes methods for:
 * - Calculating the next available day from a list of repeat days.
 * - Calculating an exact trigger time for any given time of day (AM/PM).
 *
 * The functions support both 12-hour and 24-hour time formats.
 *
 * Functions:
 * - getNextAvailableDay: Calculates the number of days until the next available repeat day.
 * - calculateTriggerTime: Computes the exact trigger time based on hour, minute, and AM/PM.
 */
@Singleton
object SchedulerUtils {

    /**
     * Calculates the number of days until the next available day in the repeatDays list.
     * If the alarm is in the past and there are no repeat days, it returns 1.
     * If there are repeat days, it returns the number of days until the next available repeat day.
     * Returns:
     * - 0 if today is the next available day and the alarm is in the future.
     * - A positive integer representing the number of days until the next available repeat day.
     */
    fun getNextAvailableDay(calendar: Calendar, repeatDays: List<DayOfWeek>): Int {
        if (repeatDays.isEmpty()) {
            return if (calendar.timeInMillis <= System.currentTimeMillis()) 1 else 0
        }

        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val currentDayOfWeek = DayOfWeek.values().first { it.ordinal + 1 == today }
        val sortedDays = repeatDays.sortedBy { it.ordinal }

        for (day in sortedDays) {
            val daysDifference = day.ordinal - currentDayOfWeek.ordinal
            if (daysDifference > 0 || (daysDifference == 0 && calendar.timeInMillis > System.currentTimeMillis())) {
                return daysDifference
            }
        }

        return sortedDays.first().ordinal + 7 - currentDayOfWeek.ordinal
    }

    /**
     * Calculates the alarm trigger time based on the provided alarm time and its AM/PM status.
     * Returns the exact Calendar time for the alarm.
     */
    fun calculateAlarmTriggerTime(time: TimeOfDay): Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()

            set(Calendar.MINUTE, time.minute)
            set(Calendar.SECOND, 0)
            if (time.meridiem == null) {
                set(Calendar.HOUR_OF_DAY, time.hour)
            } else {
                val hour = if (time.hour == 12) 0 else time.hour
                set(Calendar.HOUR, hour)
                set(
                    Calendar.AM_PM,
                    if (time.meridiem == MeridiemOption.AM) Calendar.AM else Calendar.PM
                )
            }
        }
    }
}