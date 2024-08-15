package com.daisy.jetclock.core

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.daisy.jetclock.core.manager.AlarmSchedulerManager
import com.daisy.jetclock.domain.Alarm
import com.daisy.jetclock.repositories.AlarmRepository
import com.daisy.jetclock.utils.AlarmNotificationManager
import com.daisy.jetclock.utils.MediaPlayerManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmService : Service() {

    @Inject
    lateinit var mediaPlayerManager: MediaPlayerManager

    @Inject
    lateinit var notificationManager: AlarmNotificationManager

    @Inject
    lateinit var alarmRepository: AlarmRepository

    @Inject
    lateinit var alarmSchedulerManager: AlarmSchedulerManager

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val id = intent?.getLongExtra(IntentExtra.ID_EXTRA, -1) ?: -1
        val snoozedTimestamp = intent?.getStringExtra(IntentExtra.SNOOZED_TIMESTAMP_EXTRA) ?: ""

        CoroutineScope(Dispatchers.Main).launch {
            alarmRepository.getAlarmById(id).first()?.let { alarm ->
                when (intent?.action) {
                    ACTION_START -> startAlarm(alarm, snoozedTimestamp)
                    ACTION_SNOOZE -> snoozeAlarm(alarm)
                    ACTION_DISMISS -> dismissAlarm(alarm)
                    ACTION_MISSED -> {}
                }
            } ?: stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun startAlarm(alarm: Alarm, timestamp: String) {
        notificationManager.removeAlarmSnoozedNotification()
        mediaPlayerManager.prepare("")
        mediaPlayerManager.start()

        val displayTimestamp = timestamp.ifEmpty { alarm.timestamp }

        startForeground(
            NotificationConfig.ALARM_UPCOMING_NOTIFICATION_ID,
            notificationManager.getAlarmNotification(alarm.id, alarm.label, displayTimestamp)
        )

        Handler(Looper.getMainLooper()).postDelayed({
            stopSelf()
        }, 1 * 60 * 1000)
    }

    private fun snoozeAlarm(alarm: Alarm) {
        mediaPlayerManager.release()

        alarmSchedulerManager.snooze(alarm).also {
            notificationManager.showAlarmSnoozedNotification(it.label, it.timestamp)
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private suspend fun dismissAlarm(alarm: Alarm) {
        mediaPlayerManager.release()
        stopForeground(STOP_FOREGROUND_REMOVE)

        if (alarm.repeatDays.isEmpty()) {
            alarm.isEnabled = false
            alarmRepository.insertAlarm(alarm)
        }

        stopSelf()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        notificationManager.removeAlarmUpcomingNotification()
        mediaPlayerManager.release()
    }

    companion object {
        const val ACTION_START = "ACTION_START_ALARM"
        const val ACTION_SNOOZE = "ACTION_SNOOZE_ALARM"
        const val ACTION_DISMISS = "ACTION_DISMISS_ALARM"
        const val ACTION_MISSED = "ACTION_MISSED_ALARM"
    }
}