package com.daisy.jetclock.presentation.utils.formatter

import android.content.Context
import com.daisy.jetclock.R
import com.daisy.jetclock.domain.model.DayOfWeek
import com.daisy.jetclock.domain.model.RepeatDays

object RepeatDaysFormatter {

    fun formatRepeatDaysString(context: Context, repeatDays: RepeatDays): String {
        return with(repeatDays) {
            when {
                days.isEmpty() -> context.getString(R.string.ring_once)

                days.size == 7 -> context.getString(R.string.everyday)

                days.size == 5 && days.none {
                    it == DayOfWeek.SATURDAY || it == DayOfWeek.SUNDAY
                } -> context.getString(
                    R.string.monday_to_friday
                )

                else -> sort().days.joinToString { day -> day.abbr }
            }
        }
    }
}