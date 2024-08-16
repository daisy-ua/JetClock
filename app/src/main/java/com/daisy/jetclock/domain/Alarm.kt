package com.daisy.jetclock.domain

import android.annotation.SuppressLint
import com.daisy.jetclock.constants.MeridiemOption

data class Alarm(
    val id: Long,

    val hour: Int,

    val minute: Int,

    val meridiem: MeridiemOption?,

    val repeatDays: List<DayOfWeek>,

    var isEnabled: Boolean,

    val label: String,

    val ringDuration: Int,

    val snoozeDuration: Int,

    val snoozeNumber: Int,

    val snoozeCount: Int,

    val sound: String?,
) {
    val timestamp: String
        @SuppressLint("DefaultLocale")
        get() = "$hour:${String.format("%02d", minute)} ${meridiem ?: ""}"
}


