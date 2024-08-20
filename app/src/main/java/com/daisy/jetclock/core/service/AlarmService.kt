package com.daisy.jetclock.core.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.daisy.jetclock.core.IntentExtra
import com.daisy.jetclock.core.NotificationConfig
import com.daisy.jetclock.core.manager.AlarmSchedulerManager
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
    lateinit var alarmSchedulerManager: AlarmSchedulerManager

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

    private suspend fun autoSnoozeAlarm(alarm: Alarm) {
        val updatedAlarm = alarm.run {
            if (snoozeCount < snoozeNumber) {
                copy(snoozeCount = snoozeCount + 1).also {
                    performSnoozeAction(it)
                }
            } else {
                val isScheduled = performDismissAction(this)
                notificationManager.showAlarmMissedNotification(label, timestamp)
                copy(snoozeCount = 0, isEnabled = isScheduled)
            }
        }

        alarmRepository.insertAlarm(updatedAlarm)
    }

    private fun performSnoozeAction(alarm: Alarm) {
        mediaPlayerManager.release()

        alarmSchedulerManager.snooze(alarm).also {
            notificationManager.showAlarmSnoozedNotification(it.label, it.timestamp)
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun performDismissAction(alarm: Alarm): Boolean {
        mediaPlayerManager.release()
        stopForeground(STOP_FOREGROUND_REMOVE)

        if (alarm.repeatDays.isEmpty()) {
            alarm.isEnabled = false
        } else {
            alarmSchedulerManager.reschedule(alarm)
        }

        return alarm.isEnabled
    }

    private suspend fun snoozeAlarm(alarm: Alarm) {
        performSnoozeAction(alarm)
        if (alarm.snoozeCount > 0) {
            alarmRepository.insertAlarm(alarm.copy(snoozeCount = 0))
        }
        stopSelf()
    }

    private suspend fun dismissAlarm(alarm: Alarm) {
        performDismissAction(alarm)
        alarmRepository.insertAlarm(alarm.copy(snoozeCount = 0))
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