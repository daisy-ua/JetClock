package com.daisy.jetclock.domain.model

data class RepeatDays(
    val days: List<DayOfWeek>,
) {
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