package com.daisy.jetclock.domain.model

import android.os.Parcelable
import com.daisy.jetclock.constants.DefaultAlarmConfig
import kotlinx.parcelize.Parcelize

@Parcelize
data class SoundOption(
    val soundFile: String,
) : Parcelable {
    val isSoundNone: Boolean
        get() = soundFile == NONE_FILENAME

    companion object {

        val default: SoundOption
            get() = SoundOption(
                soundFile = DefaultAlarmConfig.DEFAULT_SOUND_ID,
            )

        val none: SoundOption
            get() = SoundOption(
                soundFile = NONE_FILENAME,
            )

        const val NONE_FILENAME = "NO_SOUND"
    }
}
