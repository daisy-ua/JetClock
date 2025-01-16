package com.daisy.jetclock.core.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.daisy.jetclock.core.media.PlaybackService
import com.daisy.jetclock.core.notification.foreground.ForegroundServiceNotification
import com.daisy.jetclock.core.notification.fullscreen.FullscreenNotificationStopper
import com.daisy.jetclock.core.utils.IntentExtra
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.usecase.GetAlarmDetailsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmMediaService : Service() {

    @Inject
    lateinit var getAlarmDetailsUseCase: GetAlarmDetailsUseCase

    @Inject
    lateinit var mediaPlaybackService: PlaybackService

    @Inject
    lateinit var fullscreenNotificationStopper: FullscreenNotificationStopper

    @Inject
    lateinit var serviceNotification: ForegroundServiceNotification<Alarm>

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var ongoingAlarmId: Long? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val id = intent?.getLongExtra(IntentExtra.ID_EXTRA, -1) ?: -1

        serviceScope.launch {
            when (intent?.action) {
                ACTION_START -> start(id)

                ACTION_STOP -> stopIfNeeded(id)
            }
        }

        return START_STICKY
    }

    private suspend fun start(id: Long) {
        val alarm = getAlarmDetailsUseCase(id).firstOrNull() ?: return

        mediaPlaybackService.startPlayback(alarm.soundOption)

        startForeground(
            alarm.id.toInt(),
            serviceNotification.getNotification(alarm),
        )

        ongoingAlarmId = id
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