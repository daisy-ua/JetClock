package com.daisy.jetclock.core.manager

import android.content.Context
import android.content.Intent
import com.daisy.jetclock.constants.DefaultAlarmConfig
import com.daisy.jetclock.core.IntentExtra
import com.daisy.jetclock.core.receiver.AlarmBroadcastReceiver
import com.daisy.jetclock.domain.model.Alarm
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmActionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmController: AlarmController,
) {
    suspend fun reschedule(currentAlarm: Alarm, alarm: Alarm): Long {
        val updatedAlarm = alarmController.insert(currentAlarm)
        if (alarm.isEnabled && alarm.id != DefaultAlarmConfig.NEW_ALARM_ID) {
            cancel(alarm)
        }

        return schedule(updatedAlarm)
    }

    suspend fun schedule(alarm: Alarm): Long {
        return alarmController.schedule(alarm)
    }

    suspend fun cancel(alarm: Alarm) {
        disableMediaPlayback(alarm.id)
        alarmController.cancel(alarm)
    }

    suspend fun delete(alarm: Alarm) {
        disableMediaPlayback(alarm.id)
        alarmController.delete(alarm)
    }

    private fun disableMediaPlayback(id: Long) {
        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            action = AlarmBroadcastReceiver.ACTION_CANCEL
            putExtra(IntentExtra.ID_EXTRA, id)
        }

        context.sendBroadcast(intent)
    }
}