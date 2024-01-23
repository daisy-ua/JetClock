package com.daisy.jetclock.domain

enum class DayOfWeek(val abbr: String) {
    SUNDAY("Sun"),
    MONDAY("Mon"),
    TUESDAY("Tue"),
    WEDNESDAY("Wed"),
    THURSDAY("Thu"),
    FRIDAY("Fri"),
    SATURDAY("Sat");

    companion object {
        fun fromAbbreviation(abbr: String): DayOfWeek? {
            return values().find { it.abbr == abbr }
        }
    }
}