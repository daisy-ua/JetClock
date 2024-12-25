package com.daisy.jetclock.domain.model

import com.daisy.jetclock.constants.ConfigConstants
import com.daisy.jetclock.constants.DefaultAlarmConfig

data class SoundOption(
    val soundFile: String,
    val displayName: String,
) {

    constructor(soundFile: String) : this(
        soundFile = soundFile,
        displayName = getDisplayName(soundFile)
    )

    val isSoundNone: Boolean
        get() = soundFile == NONE_FILENAME

    companion object {
        private const val NONE_FILENAME = "NO_SOUND"

        const val NONE = "None"

        const val DEFAULT = "Default"

        val default: SoundOption
            get() = SoundOption(
                soundFile = DefaultAlarmConfig.DEFAULT_SOUND_ID,
                displayName = DEFAULT
            )

        val none: SoundOption
            get() = SoundOption(
                soundFile = NONE_FILENAME,
                displayName = NONE
            )

        fun getDisplayName(fileName: String): String {
            return when (fileName) {
                DefaultAlarmConfig.DEFAULT_SOUND_ID -> DEFAULT

                NONE_FILENAME -> NONE

                else -> getSoundName(fileName)
            }
        }

        private fun getSoundName(file: String): String {
            return try {
                file.split(".")[0]
            } catch (e: Exception) {
                file
            }
        }

        // TODO: refactor to remove
        fun getAssetFn(displayName: String): String {
            return "${ConfigConstants.SOUND_ASSETS_DIR}/$displayName"
        }
    }
}
