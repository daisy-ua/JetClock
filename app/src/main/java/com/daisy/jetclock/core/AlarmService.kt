package com.daisy.jetclock.core

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.daisy.jetclock.utils.AlarmNotificationManager
import com.daisy.jetclock.utils.MediaPlayerManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmService : Service() {

    @Inject
    lateinit var mediaPlayerManager: MediaPlayerManager

    @Inject
    lateinit var notificationManager: AlarmNotificationManager

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val title = intent?.getStringExtra("LABEL") ?: ""
        val time = intent?.getStringExtra("TIME") ?: ""
        val id = intent?.getLongExtra("ID", -1) ?: -1

        mediaPlayerManager.prepare("")
        mediaPlayerManager.start()

        startForeground(
            NotificationConfig.ALARM_UPCOMING_NOTIFICATION_ID,
            notificationManager.getAlarmNotification(id, title, time)
        )

        Handler(Looper.getMainLooper()).postDelayed({
            stopSelf()
        }, 1 * 60 * 1000)

        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        notificationManager.removeAlarmUpcomingNotification()
        mediaPlayerManager.release()
    }
}