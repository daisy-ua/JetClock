package com.daisy.jetclock.data.local.converter

import androidx.room.TypeConverter
import com.daisy.jetclock.domain.model.DayOfWeek


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
            ?.joinToString(",") { it.abbr }
            ?: ""
    }
}