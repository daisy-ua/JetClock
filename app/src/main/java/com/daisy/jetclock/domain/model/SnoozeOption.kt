package com.daisy.jetclock.domain.model

data class SnoozeOption(
    val duration: Int,
    val number: Int,
    val displayString: String,
) {
    constructor(duration: Int, number: Int) : this(
        duration = duration,
        number = number,
        displayString = "$duration minutes, ${number}x"
    )
}
