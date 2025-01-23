package com.daisy.jetclock.domain.model

import android.os.Parcelable
import androidx.compose.runtime.saveable.Saver
import kotlinx.parcelize.Parcelize

@Parcelize
data class RingDurationOption(
    val value: Int,
) : Parcelable {
    //    TODO: make simple Int
    companion object {
        fun Saver(): Saver<RingDurationOption, *> = Saver(
            save = { it to listOf(it.value) },
            restore = {
                RingDurationOption(
                    value = it.first.value,
                )
            }
        )
    }
}
