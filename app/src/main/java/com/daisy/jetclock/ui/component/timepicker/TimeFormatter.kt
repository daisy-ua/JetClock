package com.daisy.jetclock.ui.component.timepicker

import com.daisy.jetclock.constants.TimeFormat


class TimeFormatter(
    timeFormat: TimeFormat = TimeFormat.Hour12Format,
) {
    val hoursRange: List<Int> = when (timeFormat) {
        TimeFormat.Hour12Format -> (1..12).toList()

        TimeFormat.Hour24Format -> (0..23).toList()
    }

    val minutesRange: List<Int> = (0 until 60).toList()

    var hours: List<String> = convertTwoCharTime(hoursRange)
        private set

    val minutes: List<String> = convertTwoCharTime(minutesRange)

    private fun convertTwoCharTime(range: List<Int>): List<String> {
        return range.map {
            convertTwoCharTime(it)
        }
    }

    companion object {
        fun convertTwoCharTime(value: Int): String {
            return value.toString().apply {
                if (length == 1) {
                    return "0$this"
                }
            }
        }
    }
}