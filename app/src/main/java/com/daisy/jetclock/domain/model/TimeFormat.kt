package com.daisy.jetclock.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TimeFormat : Parcelable {
    Hour12Format,

    Hour24Format,
}