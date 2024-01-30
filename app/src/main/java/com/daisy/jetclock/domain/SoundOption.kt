package com.daisy.jetclock.domain

import com.daisy.jetclock.constants.ConfigConstants
import com.daisy.jetclock.constants.NewAlarmDefaults

data class SoundOption(
    val soundFile: String?,
    val displayName: String,
) {
    constructor() : this(
        soundFile = NewAlarmDefaults.DEFAULT_SOUND_ID,
        displayName = DEFAULT
    )

    constructor(soundFile: String?) : this(
        soundFile = soundFile,
        displayName = getDisplayName(soundFile)
    )

    companion object {
        const val NONE = "None"

        const val DEFAULT = "Default"

        fun getDisplayName(fileName: String?): String {
            return fileName?.let { file ->
                if (file == NewAlarmDefaults.DEFAULT_SOUND_ID)
                    DEFAULT
                else getSoundName(file)
            } ?: NONE
        }

        fun getSoundName(file: String?): String? {
            return file?.split(".")?.get(0)
        }

        fun getAssetFilename(displayName: String): String {
            return "${ConfigConstants.SOUND_ASSETS_DIR}/$displayName.mp3"
        }
    }
}
