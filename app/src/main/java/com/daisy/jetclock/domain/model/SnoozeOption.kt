package com.daisy.jetclock.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SnoozeOption(
    val duration: Int,
    val number: Int,
) : Parcelable
