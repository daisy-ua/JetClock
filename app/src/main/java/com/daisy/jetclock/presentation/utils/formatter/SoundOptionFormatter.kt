package com.daisy.jetclock.presentation.utils.formatter

import android.content.Context
import com.daisy.jetclock.R
import com.daisy.jetclock.constants.DefaultAlarmConfig
import com.daisy.jetclock.domain.model.SoundOption
import com.daisy.jetclock.domain.model.SoundOption.Companion.NONE_FILENAME

object SoundOptionFormatter {

    fun getDisplayName(context: Context, sound: SoundOption): String {
        return when (sound.soundFile) {
            DefaultAlarmConfig.DEFAULT_SOUND_ID -> context.getString(R.string.sound_default_display)

            NONE_FILENAME -> context.getString(R.string.sound_none_display)

            else -> getSoundName(sound.soundFile)
        }
    }

    private fun getSoundName(file: String): String {
        return try {
            file.split(".")[0]
        } catch (e: Exception) {
            file
        }
    }
}