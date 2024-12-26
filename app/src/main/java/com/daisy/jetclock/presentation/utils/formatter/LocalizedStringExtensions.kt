package com.daisy.jetclock.presentation.utils.formatter

import android.content.Context
import com.daisy.jetclock.R
import com.daisy.jetclock.constants.MeridiemOption
import com.daisy.jetclock.domain.model.DayOfWeek

internal fun DayOfWeek.getLocalizedString(context: Context): String {
    return when (this) {
        DayOfWeek.SUNDAY -> context.getString(R.string.day_sun)
        DayOfWeek.MONDAY -> context.getString(R.string.day_mon)
        DayOfWeek.TUESDAY -> context.getString(R.string.day_tue)
        DayOfWeek.WEDNESDAY -> context.getString(R.string.day_wed)
        DayOfWeek.THURSDAY -> context.getString(R.string.day_thu)
        DayOfWeek.FRIDAY -> context.getString(R.string.day_fri)
        DayOfWeek.SATURDAY -> context.getString(R.string.day_sat)
    }
}

internal fun MeridiemOption.getLocalizedString(context: Context): String {
    return when (this) {
        MeridiemOption.AM -> context.getString(R.string.meridiem_am)
        MeridiemOption.PM -> context.getString(R.string.meridiem_pm)
    }
}