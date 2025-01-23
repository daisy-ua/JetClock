package com.daisy.jetclock.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RepeatDays(
    val days: List<DayOfWeek>,
) : Parcelable {
    fun sort(firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY): RepeatDays {
        val startIndex = firstDayOfWeek.ordinal
        return days
            .map { days -> days.ordinal }
            .sortedBy { ordinal -> (ordinal - startIndex + 7) % 7 }
            .map { day -> DayOfWeek.values()[day] }
            .let { days ->
                RepeatDays(days)
            }
    }
}