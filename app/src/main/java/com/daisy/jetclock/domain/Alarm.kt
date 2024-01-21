package com.daisy.jetclock.domain

import com.daisy.jetclock.constants.DayOfWeek
import com.daisy.jetclock.constants.MeridiemOption

data class Alarm(
    val id: Long,

    val hour: Int,

    val minute: Int,

    val meridiem: MeridiemOption?,

    val repeatDays: List<DayOfWeek>,

    val isEnabled: Boolean,

    val label: String,

    val ringDuration: Int,

    val snoozeDuration: Int,

    val snoozeNumber: Int,

    val sound: String?,
)


