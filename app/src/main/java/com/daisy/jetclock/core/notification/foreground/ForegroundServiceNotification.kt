package com.daisy.jetclock.core.notification.foreground

import android.app.Notification

interface ForegroundServiceNotification<T> {

    fun getNotification(data: T): Notification
}