package com.daisy.jetclock.domain.model

import androidx.compose.runtime.saveable.Saver

data class RingDurationOption(
    val value: Int,
    val displayString: String,
) {
    constructor(duration: Int) : this(
        value = duration,
        displayString = if (duration == 1) "$duration minute" else "$duration minutes"
    )

    companion object {
        fun Saver(): Saver<RingDurationOption, *> = Saver(
            save = { it to listOf(it.value, it.displayString) },
            restore = {
                RingDurationOption(
                    value = it.first.value,
                    displayString = it.first.displayString
                )
            }
        )
    }
}
