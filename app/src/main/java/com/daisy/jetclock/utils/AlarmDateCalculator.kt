package com.daisy.jetclock.utils

import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.model.DayOfWeek
import java.util.Calendar
import javax.inject.Singleton

@Singleton
object AlarmDateCalculator {

    //    todo: simplify complex conditions
    fun getNextAvailableDay(calendar: Calendar, repeatDays: List<DayOfWeek>): Int {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

        if (repeatDays.isEmpty() && calendar.timeInMillis <= System.currentTimeMillis()) {
            return 1
        }

        if (repeatDays.isEmpty()) {
            return 0
        }

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

    fun calculateAlarmTriggerTime(alarm: Alarm): Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            if (alarm.meridiem == null) {
                set(
                    Calendar.HOUR_OF_DAY,
                    alarm.hour.let {
                        if (it == 12) 0 else it
                    })
            } else {
                set(Calendar.HOUR, alarm.hour)
                set(Calendar.AM_PM, alarm.meridiem.ordinal)
            }
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
        }
    }
}