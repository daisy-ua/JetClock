package com.daisy.jetclock.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Alarm(
    val id: Long,

    val time: TimeOfDay,

    val repeatDays: RepeatDays,

    val isEnabled: Boolean,

    val triggerTime: Long?,

    val label: String,

    val ringDurationOption: RingDurationOption,

    val snoozeOption: SnoozeOption,

// Tracks the number of times alarm has been snoozed
    val snoozeCount: Int,

    val soundOption: SoundOption,
) {
    val timestamp: String
        get() = time.timestamp
}


