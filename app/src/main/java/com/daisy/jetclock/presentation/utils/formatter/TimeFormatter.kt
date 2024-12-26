package com.daisy.jetclock.presentation.utils.formatter

import android.content.Context
import com.daisy.jetclock.R
import com.daisy.jetclock.domain.model.TimeOfDay

object TimeFormatter {

    fun formatTime(context: Context, time: TimeOfDay): String {
        return context.getString(R.string.time_format, time.hour, time.minute, "")
    }

    fun formatTimeWithMeridiem(context: Context, time: TimeOfDay): String {
        val amPm = time.meridiem?.getLocalizedString(context) ?: ""
        return context.getString(R.string.time_format, time.hour, time.minute, amPm)
    }
}