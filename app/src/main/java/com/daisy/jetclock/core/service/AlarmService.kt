package com.daisy.jetclock.core.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.daisy.jetclock.core.IntentExtra
import com.daisy.jetclock.core.NotificationConfig
import com.daisy.jetclock.core.manager.AlarmController
import com.daisy.jetclock.domain.Alarm
import com.daisy.jetclock.repositories.AlarmRepository
import com.daisy.jetclock.utils.AlarmNotificationManager
import com.daisy.jetclock.utils.MediaPlayerManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
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
    lateinit var alarmController: AlarmController

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val id = intent?.getLongExtra(IntentExtra.ID_EXTRA, -1) ?: -1
        val snoozedTimestamp = intent?.getStringExtra(IntentExtra.SNOOZED_TIMESTAMP_EXTRA) ?: ""

        serviceScope.launch {
            handleIntentAction(intent, id, snoozedTimestamp)
        }

        return START_NOT_STICKY
    }

    private suspend fun handleIntentAction(intent: Intent?, id: Long, snoozedTimestamp: String) {
        val alarm = alarmRepository.getAlarmById(id).first()
        alarm?.let {
            when (intent?.action) {
                ACTION_START -> startAlarm(it, snoozedTimestamp)
                ACTION_SNOOZE -> snoozeAlarm(it)
                ACTION_DISMISS -> dismissAlarm(it)
            }
        } ?: stopSelf()
    }

    private fun startAlarm(alarm: Alarm, timestamp: String) {
        notificationManager.removeAlarmSnoozedNotification()
        mediaPlayerManager.prepare(alarm.sound, true)
        mediaPlayerManager.start()

        val displayTimestamp = timestamp.ifEmpty { alarm.timestamp }

        startForeground(
            NotificationConfig.ALARM_UPCOMING_NOTIFICATION_ID,
            notificationManager.getAlarmNotification(alarm.id, alarm.label, displayTimestamp)
        )

        scheduleAutoSnooze(alarm)
    }

    private fun scheduleAutoSnooze(alarm: Alarm) {
        Handler(Looper.getMainLooper()).postDelayed({
            serviceScope.launch {
                autoSnoozeAlarm(alarm)
                stopSelf()
            }
        }, alarm.ringDuration * 60 * 1000L)
    }

    private fun autoSnoozeAlarm(alarm: Alarm) {
        if (alarm.snoozeCount < alarm.snoozeNumber) {
            val updatedAlarm = alarm.copy(snoozeCount = alarm.snoozeCount + 1)
            performSnoozeAction(updatedAlarm)
        } else {
            performDismissAction(alarm)
            notificationManager.showAlarmMissedNotification(alarm.label, alarm.timestamp)
        }
    }

    private fun performSnoozeAction(alarm: Alarm) {
        mediaPlayerManager.release()

        val updatedAlarm = alarmController.snooze(alarm)
        notificationManager.showAlarmSnoozedNotification(updatedAlarm.label, updatedAlarm.timestamp)

        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun performDismissAction(alarm: Alarm) {
        mediaPlayerManager.release()
        stopForeground(STOP_FOREGROUND_REMOVE)

        val updatedAlarm = alarm.copy(
            snoozeCount = 0
        )

        if (alarm.repeatDays.isEmpty()) {
            updatedAlarm.triggerTime = null
            updatedAlarm.isEnabled = false

            alarmController.updateAlarm(updatedAlarm)

        } else {
            alarmController.schedule(alarm)
        }
    }

    private fun snoozeAlarm(alarm: Alarm) {
        performSnoozeAction(alarm)
        if (alarm.snoozeCount > 0) {
            val updatedAlarm = alarm.copy(
                snoozeCount = 0
            )
            alarmController.updateAlarm(updatedAlarm)
        }
        stopSelf()
    }

    private fun dismissAlarm(alarm: Alarm) {
        performDismissAction(alarm)
        stopSelf()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        serviceScope.cancel()
        notificationManager.removeAlarmUpcomingNotification()
        mediaPlayerManager.release()
    }

    companion object {
        const val ACTION_START = "ACTION_START_ALARM"
        const val ACTION_SNOOZE = "ACTION_SNOOZE_ALARM"
        const val ACTION_DISMISS = "ACTION_DISMISS_ALARM"
    }
}