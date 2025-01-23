package com.daisy.jetclock.core.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.daisy.jetclock.core.media.PlaybackService
import com.daisy.jetclock.core.notification.foreground.ForegroundServiceNotification
import com.daisy.jetclock.core.notification.fullscreen.FullscreenNotificationStopper
import com.daisy.jetclock.core.utils.IntentExtra
import com.daisy.jetclock.domain.model.Alarm
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmMediaService : Service() {

    @Inject
    lateinit var mediaPlaybackService: PlaybackService

    @Inject
    lateinit var fullscreenNotificationStopper: FullscreenNotificationStopper

    @Inject
    lateinit var serviceNotification: ForegroundServiceNotification<Alarm>

    private var ongoingAlarmId: Long? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val id = intent?.getLongExtra(IntentExtra.ID_EXTRA, -1) ?: -1

        val alarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(IntentExtra.DATA_EXTRA, Alarm::class.java)
        } else {
            intent?.getParcelableExtra(IntentExtra.DATA_EXTRA)
        }

        when (intent?.action) {
            ACTION_START -> alarm?.let { start(it) }

            ACTION_STOP -> stopIfNeeded(id)
        }

        return START_STICKY
    }

    private fun start(alarm: Alarm) {
        mediaPlaybackService.startPlayback(alarm.soundOption)

        startForeground(
            alarm.id.toInt(),
            serviceNotification.getNotification(alarm),
        )

        ongoingAlarmId = alarm.id
    }

    private fun stopIfNeeded(id: Long) {
        if (id == ongoingAlarmId) {
            ongoingAlarmId = null
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fullscreenNotificationStopper.stopNotification()
        mediaPlaybackService.stopPlayback()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {
        const val ACTION_START = "ACTION_START_SERVICE"
        const val ACTION_STOP = "ACTION_STOP_SERVICE"
    }
}