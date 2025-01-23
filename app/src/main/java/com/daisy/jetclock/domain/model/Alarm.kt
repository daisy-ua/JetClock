package com.daisy.jetclock.domain.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class Alarm(
    val id: Long,

    val time: TimeOfDay,

    val repeatDays: RepeatDays,

    val isEnabled: Boolean,

    val triggerTime: Long?,

    val label: String,

    val ringDuration: Int,

    val snoozeOption: SnoozeOption,

// Tracks the number of times alarm has been snoozed
    val snoozeCount: Int,

    val soundOption: SoundOption,
) : Parcelable