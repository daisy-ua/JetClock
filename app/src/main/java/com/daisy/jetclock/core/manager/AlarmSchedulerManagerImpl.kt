package com.daisy.jetclock.core.manager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.daisy.jetclock.core.receiver.AlarmBroadcastReceiver
import com.daisy.jetclock.constants.ConfigConstants
import com.daisy.jetclock.domain.Alarm
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    private val context: Context,
) : AlarmScheduler {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val handler = Handler(Looper.getMainLooper())

    @SuppressLint("ScheduleExactAlarm")
    override fun schedule(alarm: Alarm) {
        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            putExtra("LABEL", "Alarm")
        }

        val calendar: Calendar = getTimeInstance(alarm)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
//        calendar.timeInMillis,
            LocalDateTime.now().plusSeconds(5).atZone(ZoneId.systemDefault())
                .toEpochSecond() * 1000L,
            PendingIntent.getBroadcast(
                context,
                alarm.id.toInt(),
                intent,
                ConfigConstants.PENDING_INTENT_FLAGS
            )
        )

        val toastText = "Alarm rings soon"

        handler.post {
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
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

