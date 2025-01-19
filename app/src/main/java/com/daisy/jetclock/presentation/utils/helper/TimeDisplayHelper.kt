package com.daisy.jetclock.presentation.utils.helper

import com.daisy.jetclock.constants.TimeFormat
import java.util.Locale

/**
 * Helper class for managing and formatting time values for a TimePicker.
 * It supports both 12-hour and 24-hour time formats, providing the necessary methods to retrieve
 * hour and minute values, as well as their string representations with leading zeros.
 *
 * @param timeFormat The time format to use (default is 12-hour format).
 */
class TimeDisplayHelper(
    timeFormat: TimeFormat = TimeFormat.Hour12Format,
) {
    private val hoursRange: List<Int> = getHoursRangeForFormat(timeFormat)

    private val minutesRange: List<Int> = (0 until 60).toList()

    var hours: List<String> = convertTwoCharTime(hoursRange)
        private set

    val minutes: List<String> = convertTwoCharTime(minutesRange)

    //    TODO: do I really need this?
    fun getHourValue(index: Int): Int {
        return hoursRange[index]
    }

    fun getHourIndex(value: Int): Int {
        return hoursRange.indexOf(value)
    }

    fun getMinuteIndex(value: Int): Int {
        return minutesRange.indexOf(value)
    }

    private fun getHoursRangeForFormat(timeFormat: TimeFormat): List<Int> {
        return when (timeFormat) {
            TimeFormat.Hour12Format -> 1..12

            TimeFormat.Hour24Format -> 0..23
        }.toList()
    }

    private fun convertTwoCharTime(range: List<Int>): List<String> {
        return range.map { String.format(Locale.ROOT, "%02d", it) }
    }
}