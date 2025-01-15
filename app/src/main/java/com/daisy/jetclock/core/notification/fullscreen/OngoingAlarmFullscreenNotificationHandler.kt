package com.daisy.jetclock.core.notification.fullscreen

import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OngoingAlarmFullscreenNotificationHandler @Inject constructor(
    @ApplicationContext private val context: Context,
) : FullscreenNotificationStopper {

    companion object {
        const val ACTION_FINISH = "ONGOING_ALARM_ACTION_FINISH"
    }

    override fun stopNotification() {
        val intent = Intent(ACTION_FINISH)
        context.sendBroadcast(intent)
    }
}