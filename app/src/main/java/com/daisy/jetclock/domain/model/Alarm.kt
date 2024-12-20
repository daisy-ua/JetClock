package com.daisy.jetclock.domain.model

import android.annotation.SuppressLint
import androidx.compose.runtime.Immutable
import com.daisy.jetclock.constants.MeridiemOption

@Immutable
data class Alarm(
    val id: Long,

    val hour: Int,

    val minute: Int,

    val meridiem: MeridiemOption?,

    val repeatDays: List<DayOfWeek>,

    val isEnabled: Boolean,

    val triggerTime: Long?,

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

