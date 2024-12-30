package com.daisy.jetclock.core.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.daisy.jetclock.constants.ConfigConstants
import com.daisy.jetclock.core.utils.IntentExtra
import com.daisy.jetclock.core.receiver.AlarmBroadcastReceiver
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.presentation.utils.formatter.TimeFormatter
import com.daisy.jetclock.utils.AlarmDateCalculator
import java.util.Calendar
import javax.inject.Inject

internal class AlarmSchedulerManagerImpl @Inject constructor(
    private val context: Context,
) : AlarmSchedulerManager {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(alarm: Alarm): Long {
        val calendar: Calendar = AlarmDateCalculator.calculateAlarmTriggerTime(alarm).apply {
            val nextDayOffset = AlarmDateCalculator.getNextAvailableDay(this, alarm.repeatDays.days)
            add(Calendar.DAY_OF_MONTH, nextDayOffset)
        }
        schedule(calendar.timeInMillis, alarm.id.toInt(), getDefaultIntent(alarm.id))

        return calendar.timeInMillis
    }

    override fun snooze(alarm: Alarm): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            add(Calendar.MINUTE, alarm.snoozeOption.duration)
        }
        val intent = getDefaultIntent(alarm.id).apply {
            putExtra(
                IntentExtra.SNOOZED_TIMESTAMP_EXTRA,
                TimeFormatter.formatTimeWithMeridiem(context, alarm.time)
            )
        }
        schedule(calendar.timeInMillis, alarm.id.toInt(), intent)

        return calendar.timeInMillis
    }

    override fun cancel(alarm: Alarm) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alarm.id.toInt(),
                Intent(context, AlarmBroadcastReceiver::class.java),
                ConfigConstants.PENDING_INTENT_FLAGS
            )
        )
    }

    private fun schedule(timeInMillis: Long, id: Int, intent: Intent) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            ConfigConstants.PENDING_INTENT_FLAGS
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
    }

    private fun getDefaultIntent(alarmId: Long) =
        Intent(context, AlarmBroadcastReceiver::class.java).apply {
            putExtra(IntentExtra.ID_EXTRA, alarmId)
        }
}

