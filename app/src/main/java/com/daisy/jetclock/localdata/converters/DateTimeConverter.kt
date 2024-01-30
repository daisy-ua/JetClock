package com.daisy.jetclock.localdata.converters

import androidx.room.TypeConverter
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object DateTimeConverter {
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")

    @TypeConverter
    @JvmStatic
    fun toLocalTime(value: String): LocalTime =
        formatter.parse(value, LocalTime::from)

    @TypeConverter
    @JvmStatic
    fun fromLocalTime(dateTime: LocalTime): String =
        dateTime.format(formatter)
}