package com.daisy.jetclock.ui.component.timepicker

import com.daisy.jetclock.constants.TimeFormat


object TimeFormatter {
    var timeFormat: TimeFormat = TimeFormat.Hour12Format
        private set

    fun setTimeFormat(timeFormat: TimeFormat) {
        this.timeFormat = timeFormat
    }

    var hours: List<String> = calculateHours()
        private set

    private fun calculateHours(): List<String> {
        return when (timeFormat) {
            TimeFormat.Hour12Format -> convertTwoCharTime(1..12)

            TimeFormat.Hour24Format -> convertTwoCharTime(1..24)
        }
    }

    val minutes: List<String> = convertTwoCharTime(0 until 60)

    private fun convertTwoCharTime(value: Int): String {
        return value.toString().apply {
            if (length == 1) {
                return "0$this"
            }
        }
    }

    private fun convertTwoCharTime(range: IntRange): List<String> {
        return range.map {
            convertTwoCharTime(it)
        }
    }
}