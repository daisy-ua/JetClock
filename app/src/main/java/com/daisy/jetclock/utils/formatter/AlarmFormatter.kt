package com.daisy.jetclock.utils.formatter

import android.content.Context
import com.daisy.jetclock.R

object AlarmFormatter {
    fun getTimeString(context: Context, hour: Int, minute: Int): String {
        val formattedMinute = TimeFormatter.convertTwoCharTime(minute)
        return context.getString(R.string.time_format, hour, formattedMinute)
    }
}