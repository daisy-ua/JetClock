package com.daisy.jetclock.domain

import com.daisy.jetclock.constants.MeridiemOption

data class TimeOfDay(
    val hour: Int,
    val minute: Int,
    val meridiem: MeridiemOption?
)
