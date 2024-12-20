package com.daisy.jetclock.domain.model

import android.annotation.SuppressLint
import com.daisy.jetclock.constants.MeridiemOption

data class TimeOfDay(
    val hour: Int,
    val minute: Int,
    val meridiem: MeridiemOption?
) {
    val timestamp: String
        @SuppressLint("DefaultLocale")
        get() = "$hour:${String.format("%02d", minute)} ${meridiem ?: ""}"
}
