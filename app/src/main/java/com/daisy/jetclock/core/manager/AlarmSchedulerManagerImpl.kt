package com.daisy.jetclock.core.manager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.daisy.jetclock.constants.ConfigConstants
import com.daisy.jetclock.constants.MeridiemOption
import com.daisy.jetclock.core.IntentExtra
import com.daisy.jetclock.core.receiver.AlarmBroadcastReceiver
import com.daisy.jetclock.domain.Alarm
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import javax.inject.Inject

class AlarmSchedulerManagerImpl @Inject constructor(
    private val context: Context,
) : AlarmSchedulerManager {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val handler = Handler(Looper.getMainLooper())

    @SuppressLint("ScheduleExactAlarm")
    override fun schedule(alarm: Alarm) {
        val calendar: Calendar = getTimeInstance(alarm)

        schedule(calendar.timeInMillis, alarm.id, getDefaultIntent(alarm.id))

        val toastText = "Alarm rings soon"

        handler.post {
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
        }
    }

    private fun schedule(timeInMillis: Long, alarmId: Long, intent: Intent) {

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            ConfigConstants.PENDING_INTENT_FLAGS
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
//        calendar.timeInMillis,
            LocalDateTime.now().plusSeconds(3).atZone(ZoneId.systemDefault())
                .toEpochSecond() * 1000L,
            pendingIntent
        )
    }

    override fun snooze(alarm: Alarm): Alarm {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            add(Calendar.MINUTE, alarm.snoozeDuration)
        }

        return alarm.copy(
            hour = calendar.get(Calendar.HOUR),
            minute = calendar.get(Calendar.MINUTE),
            meridiem = when (calendar.get(Calendar.AM_PM)) {
                Calendar.AM -> MeridiemOption.AM
                Calendar.PM -> MeridiemOption.PM
                else -> throw IllegalArgumentException("Unknown Meridiem value")
            },
        ).also {
            val intent = getDefaultIntent(it.id).apply {
                putExtra(IntentExtra.SNOOZED_TIMESTAMP_EXTRA, it.timestamp)
            }
            schedule(calendar.timeInMillis, it.id, intent)
        }
    }

    override fun cancel(id: Long) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                id.toInt(),
                Intent(context, AlarmBroadcastReceiver::class.java),
                ConfigConstants.PENDING_INTENT_FLAGS
            )
        )
    }

    private fun getDefaultIntent(alarmId: Long) =
        Intent(context, AlarmBroadcastReceiver::class.java).apply {
            putExtra(IntentExtra.ID_EXTRA, alarmId)
        }

    private fun getTimeInstance(alarm: Alarm): Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            if (alarm.meridiem == null) set(Calendar.HOUR_OF_DAY, alarm.hour)
            else {
                set(Calendar.HOUR, alarm.hour)
                set(Calendar.AM_PM, alarm.meridiem.ordinal)
            }
            set(Calendar.MINUTE, alarm.minute)
        }
    }
}

