package com.daisy.jetclock.core.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.daisy.jetclock.core.IntentExtra
import com.daisy.jetclock.core.alarm.AlarmCoordinator
import com.daisy.jetclock.core.notification.AlarmNotificationManager
import com.daisy.jetclock.core.notification.AlarmNotificationType
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.repository.AlarmRepository
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
    lateinit var notificationManager: AlarmNotificationManager

    @Inject
    lateinit var alarmRepository: AlarmRepository

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var foregroundNotification: AlarmNotificationType.Ongoing? = null

    @Inject
    lateinit var coordinator: AlarmCoordinator

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val id = intent?.getLongExtra(IntentExtra.ID_EXTRA, -1) ?: -1
        val snoozedTimestamp = intent?.getStringExtra(IntentExtra.SNOOZED_TIMESTAMP_EXTRA) ?: ""

        serviceScope.launch {
            try {
                handleIntentAction(intent, id, snoozedTimestamp)
            } catch (e: Exception) {
                Log.e("AlarmService", "Error handling intent action", e)
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    private suspend fun handleIntentAction(intent: Intent?, id: Long, snoozedTimestamp: String) {
        val alarm = alarmRepository.getAlarmById(id).firstOrNull() ?: return stopSelf()

        when (intent?.action) {
            ACTION_START -> start(alarm, snoozedTimestamp)
            ACTION_SNOOZE -> snooze(alarm)
            ACTION_DISMISS -> dismiss(alarm)
            ACTION_DISABLE -> cancel(alarm)
        }
    }

    private suspend fun start(alarm: Alarm, timestamp: String) {
        foregroundNotification = coordinator.start(alarm, timestamp) { autoSnoozeCompleted ->
            if (autoSnoozeCompleted) {
                stopSelf()
            }
        }

        foregroundNotification?.let { type ->
            startForeground(
                type.notificationId,
                notificationManager.createNotification(type)
            )
        }
    }

    private suspend fun snooze(alarm: Alarm) {
        stopForeground(STOP_FOREGROUND_REMOVE)
        coordinator.snooze(alarm)
        foregroundNotification = null
        stopSelf()
    }

    private suspend fun dismiss(alarm: Alarm) {
        stopForeground(STOP_FOREGROUND_REMOVE)
        coordinator.dismiss(alarm)
        foregroundNotification = null
        stopSelf()
    }

    private suspend fun cancel(alarm: Alarm) {
        if (foregroundNotification?.notificationId == AlarmNotificationType.Ongoing(alarm.id).notificationId) {
            coordinator.dismiss(alarm)
        }
        stopSelf()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        coordinator.cleanup()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START_ALARM"
        const val ACTION_SNOOZE = "ACTION_SNOOZE_ALARM"
        const val ACTION_DISMISS = "ACTION_DISMISS_ALARM"
        const val ACTION_DISABLE = "ACTION_DISABLE_ALARM"
    }
}