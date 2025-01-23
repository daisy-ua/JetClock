package com.daisy.jetclock.constants

import android.app.PendingIntent
import com.daisy.jetclock.domain.model.TimeFormat

object ConfigConstants {
    const val SOUND_ASSETS_DIR = "sounds"

    const val PENDING_INTENT_FLAGS =
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

    val DEFAULT_TIME_FORMAT = TimeFormat.Hour12Format
}