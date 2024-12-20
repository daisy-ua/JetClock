package com.daisy.jetclock.core.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.daisy.jetclock.core.IntentExtra
import com.daisy.jetclock.core.NotificationConfig
import com.daisy.jetclock.core.manager.AlarmController
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.repository.AlarmRepository
import com.daisy.jetclock.utils.AlarmNotificationManager
import com.daisy.jetclock.utils.MediaPlayerManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
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
        val alarm = alarmRepository.getAlarmById(id).firstOrNull() ?: return stopSelf()

        when (intent?.action) {
            ACTION_START -> startAlarm(alarm, snoozedTimestamp)
            ACTION_SNOOZE -> snoozeAlarm(alarm)
            ACTION_DISMISS -> dismissAlarm(alarm)
            ACTION_DISABLE -> disableAlarm(alarm)
        }
    }

    private fun startAlarm(alarm: Alarm, timestamp: String) {
        activeAlarmId = alarm.id
        notificationManager.removeAlarmSnoozedNotification()
        startMediaPlayback(alarm.soundOption.soundFile)

        val displayTimestamp = timestamp.ifEmpty { alarm.timestamp }

        startForeground(
            NotificationConfig.ALARM_UPCOMING_NOTIFICATION_ID,
            notificationManager.getAlarmNotification(alarm.id, alarm.label, displayTimestamp)
        )

        scheduleAutoSnooze(alarm)
    }

    private suspend fun snoozeAlarm(alarm: Alarm) {
        val updatedAlarm = alarmController.snooze(alarm)
        stopMediaPlayback()
        notificationManager.showAlarmSnoozedNotification(updatedAlarm.label, updatedAlarm.timestamp)

        if (alarm.snoozeCount > 0) {
            alarmController.resetAlarmSnoozeCount(alarm)
        }

        activeAlarmId = null
        stopSelf()
    }

    private suspend fun dismissAlarm(alarm: Alarm) {
        performDismissAction(alarm)
        activeAlarmId = null
        stopSelf()
    }

    private fun disableAlarm(alarm: Alarm) {
        if (activeAlarmId == alarm.id) {
            stopMediaPlayback()
            activeAlarmId = null
            stopSelf()
        }
    }

    private fun scheduleAutoSnooze(alarm: Alarm) {
        Handler(Looper.getMainLooper()).postDelayed({
            serviceScope.launch {
                autoSnoozeAlarm(alarm)
                stopSelf()
            }
        }, alarm.ringDurationOption.value * 60 * 1000L)
    }

    private suspend fun autoSnoozeAlarm(alarm: Alarm) {
        if (alarm.snoozeCount < alarm.snoozeOption.number) {
            alarmController.autoSnooze(alarm)
        } else {
            performDismissAction(alarm)
            notificationManager.showAlarmMissedNotification(alarm.label, alarm.timestamp)
        }
    }

    private suspend fun performDismissAction(alarm: Alarm) {
        stopMediaPlayback()
        if (shouldRepeatAlarm(alarm)) {
            alarmController.reschedule(alarm)
        } else {
            alarmController.cancel(alarm)
        }
    }

    private fun startMediaPlayback(sound: String?) {
        mediaPlayerManager.prepare(sound, true)
        mediaPlayerManager.start()
    }

    private fun stopMediaPlayback() {
        mediaPlayerManager.release()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun shouldRepeatAlarm(alarm: Alarm) = alarm.repeatDays.days.isNotEmpty()

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        serviceScope.cancel()
        notificationManager.removeAlarmUpcomingNotification()
        stopMediaPlayback()
        activeAlarmId = null
    }

    companion object {
        const val ACTION_START = "ACTION_START_ALARM"
        const val ACTION_SNOOZE = "ACTION_SNOOZE_ALARM"
        const val ACTION_DISMISS = "ACTION_DISMISS_ALARM"
        const val ACTION_DISABLE = "ACTION_DISABLE_ALARM"

        private var activeAlarmId: Long? = null
    }
}