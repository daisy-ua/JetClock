package com.daisy.jetclock.core.base

import android.annotation.SuppressLint
import android.app.Notification
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat

abstract class BaseNotificationManager<T : NotificationType>(
    protected val notificationManager: NotificationManagerCompat,
) {
    abstract fun createNotification(type: T): Notification

    @SuppressLint("MissingPermission")
    fun showNotification(type: T) {
        val notification = createNotification(type)
        notificationManager.notify(type.notificationId, notification)
    }

    fun hideNotification(type: T) {
        notificationManager.cancel(type.notificationId)
    }

    protected fun createNotificationChannels(channelData: List<NotificationChannelData>) {
        val channels = channelData.map { data ->
            NotificationChannelCompat.Builder(
                data.channelId,
                data.importance
            )
                .setName(data.name)
                .setDescription(data.description)
                .setSound(data.soundUri, data.audioAttributes)
                .build()
        }

        notificationManager.createNotificationChannelsCompat(channels)
    }
}