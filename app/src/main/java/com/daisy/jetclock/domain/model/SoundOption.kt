package com.daisy.jetclock.domain.model

import android.content.Context
import com.daisy.jetclock.R
import com.daisy.jetclock.constants.DefaultAlarmConfig

data class SoundOption(
    val soundFile: String,
) {
    val isSoundNone: Boolean
        get() = soundFile == NONE_FILENAME

    fun getDisplayName(context: Context): String {
        return when (soundFile) {
            DefaultAlarmConfig.DEFAULT_SOUND_ID -> context.getString(R.string.sound_default_display)

            NONE_FILENAME -> context.getString(R.string.sound_none_display)

            else -> getSoundName(soundFile)
        }
    }

    private fun getSoundName(file: String): String {
        return try {
            file.split(".")[0]
        } catch (e: Exception) {
            file
        }
    }

    companion object {

        val default: SoundOption
            get() = SoundOption(
                soundFile = DefaultAlarmConfig.DEFAULT_SOUND_ID,
            )

        val none: SoundOption
            get() = SoundOption(
                soundFile = NONE_FILENAME,
            )

        private const val NONE_FILENAME = "NO_SOUND"
    }
}
