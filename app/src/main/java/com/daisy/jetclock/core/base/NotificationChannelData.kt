package com.daisy.jetclock.core.base

import android.media.AudioAttributes
import android.net.Uri

data class NotificationChannelData(
    val channelId: String,
    val importance: Int,
    val name: String,
    val description: String,
    val soundUri: Uri? = null,
    val audioAttributes: AudioAttributes? = null,
)