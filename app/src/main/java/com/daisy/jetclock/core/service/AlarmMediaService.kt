package com.daisy.jetclock.core.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.daisy.jetclock.core.media.PlaybackService
import com.daisy.jetclock.core.notification.foreground.ForegroundServiceNotification
import com.daisy.jetclock.core.notification.fullscreen.FullscreenNotificationStopper
import com.daisy.jetclock.core.utils.IntentExtra
import com.daisy.jetclock.core.utils.getParcelableExtra
import com.daisy.jetclock.domain.model.Alarm
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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

    private var job: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val id = intent?.getLongExtra(IntentExtra.ID_EXTRA, -1) ?: -1
        val alarm = getParcelableExtra(intent, IntentExtra.DATA_EXTRA, Alarm::class.java)

        when (intent?.action) {
            ACTION_START -> alarm?.let { start(it) }

            ACTION_STOP -> stopIfNeeded(id)
        }

        return START_STICKY
    }

    private fun start(alarm: Alarm) {
        mediaPlaybackService.startPlayback(alarm.soundOption)

        job = CoroutineScope(Dispatchers.Main).launch {
            startForeground(
                alarm.id.toInt(),
                serviceNotification.getNotification(alarm),
            )
        }

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
        job?.cancel()
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