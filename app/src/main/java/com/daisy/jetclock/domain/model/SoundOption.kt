package com.daisy.jetclock.domain.model

import com.daisy.jetclock.constants.ConfigConstants
import com.daisy.jetclock.constants.NewAlarmDefaults

data class SoundOption(
    val soundFile: String?,
    val displayName: String,
) {

    constructor(soundFile: String?) : this(
        soundFile = soundFile,
        displayName = getDisplayName(soundFile)
    )

    companion object {
        const val NONE = "None"

        const val DEFAULT = "Default"

        val default: SoundOption
            get() = SoundOption(
                soundFile = NewAlarmDefaults.DEFAULT_SOUND_ID,
                displayName = DEFAULT
            )

        val none: SoundOption
            get() = SoundOption(
                soundFile = null,
                displayName = NONE
            )

        fun getDisplayName(fileName: String?): String {
            return fileName?.let { file ->
                if (file == NewAlarmDefaults.DEFAULT_SOUND_ID)
                    DEFAULT
                else getSoundName(file)
            } ?: NONE
        }

        private fun getSoundName(file: String?): String? {
            return file?.split(".")?.get(0)
        }

        fun getAssetFn(displayName: String): String {
            return "${ConfigConstants.SOUND_ASSETS_DIR}/$displayName"
        }
    }
}
