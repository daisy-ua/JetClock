package com.daisy.jetclock.constants

import android.app.PendingIntent

object ConfigConstants {
    const val SOUND_ASSETS_DIR = "sounds"

    const val PENDING_INTENT_FLAGS =
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
}