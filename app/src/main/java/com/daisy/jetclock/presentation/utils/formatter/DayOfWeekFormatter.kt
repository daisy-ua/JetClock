package com.daisy.jetclock.presentation.utils.formatter

import android.content.Context
import com.daisy.jetclock.R
import com.daisy.jetclock.domain.model.DayOfWeek

object DayOfWeekFormatter {

    fun getOneLetterAbbreviation(context: Context, dayOfWeek: DayOfWeek): String {
        return when (dayOfWeek) {
            DayOfWeek.SUNDAY -> context.getString(R.string.day_sun_one)
            DayOfWeek.MONDAY -> context.getString(R.string.day_mon_one)
            DayOfWeek.TUESDAY -> context.getString(R.string.day_tue_one)
            DayOfWeek.WEDNESDAY -> context.getString(R.string.day_wed_one)
            DayOfWeek.THURSDAY -> context.getString(R.string.day_thu_one)
            DayOfWeek.FRIDAY -> context.getString(R.string.day_fri_one)
            DayOfWeek.SATURDAY -> context.getString(R.string.day_sat_one)
        }
    }

    fun getThreeLetterAbbreviation(context: Context, dayOfWeek: DayOfWeek): String {
        return when (dayOfWeek) {
            DayOfWeek.SUNDAY -> context.getString(R.string.day_sun)
            DayOfWeek.MONDAY -> context.getString(R.string.day_mon)
            DayOfWeek.TUESDAY -> context.getString(R.string.day_tue)
            DayOfWeek.WEDNESDAY -> context.getString(R.string.day_wed)
            DayOfWeek.THURSDAY -> context.getString(R.string.day_thu)
            DayOfWeek.FRIDAY -> context.getString(R.string.day_fri)
            DayOfWeek.SATURDAY -> context.getString(R.string.day_sat)
        }
    }
}