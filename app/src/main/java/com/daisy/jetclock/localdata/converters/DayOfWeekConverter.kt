package com.daisy.jetclock.localdata.converters

import androidx.room.TypeConverter
import com.daisy.jetclock.domain.DayOfWeek


class DayOfWeekConverter {
    @TypeConverter
    fun toList(value: String?): List<DayOfWeek> {
        return value?.takeIf { it.isNotBlank() }
            ?.split(",")?.mapNotNull { DayOfWeek.fromAbbreviation(it) }
            ?: emptyList()
    }

    @TypeConverter
    fun fromList(value: List<DayOfWeek>?): String {
        return value?.takeIf { it.isNotEmpty() }
            ?.joinToString(",") { it.toString() }
            ?: ""
    }
}