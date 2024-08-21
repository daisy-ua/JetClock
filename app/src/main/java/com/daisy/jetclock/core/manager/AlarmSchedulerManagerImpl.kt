package com.daisy.jetclock.core.manager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.daisy.jetclock.constants.ConfigConstants
import com.daisy.jetclock.constants.MeridiemOption
import com.daisy.jetclock.core.IntentExtra
import com.daisy.jetclock.core.receiver.AlarmBroadcastReceiver
import com.daisy.jetclock.domain.Alarm
import com.daisy.jetclock.domain.DayOfWeek
import com.daisy.jetclock.domain.TimeUntilAlarm
import com.daisy.jetclock.utils.AlarmDataCallback
import java.util.Calendar
import javax.inject.Inject

class AlarmSchedulerManagerImpl @Inject constructor(
    private val context: Context,
) : AlarmSchedulerManager {
    private var dataCallback: AlarmDataCallback? = null

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @SuppressLint("ScheduleExactAlarm")
    override fun schedule(alarm: Alarm): String {
        val timeInMillis = reschedule(alarm)

        val timeLeft = getTimeLeftUntilAlarm(timeInMillis) ?: "Error occurred."

        return timeLeft
    }

    override fun reschedule(alarm: Alarm): Long {
        val calendar: Calendar = getTimeInstance(alarm).apply {
            val nextDayOffset = getNextAvailableDay(this, alarm.repeatDays)
            add(Calendar.DAY_OF_MONTH, nextDayOffset)
        }
        schedule(calendar.timeInMillis, alarm, getDefaultIntent(alarm.id))

        return calendar.timeInMillis
    }

    override fun snooze(alarm: Alarm): Alarm {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            add(Calendar.MINUTE, alarm.snoozeDuration)
        }

        return alarm.copy(
            hour = calendar.get(Calendar.HOUR).let { if (it == 0) 12 else it },
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
            schedule(calendar.timeInMillis, it, intent)
        }
    }

    override fun cancel(alarm: Alarm) {
        updateAlarm(alarm, null)

        disable(alarm)
    }

    override fun disable(alarm: Alarm) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alarm.id.toInt(),
                Intent(context, AlarmBroadcastReceiver::class.java),
                ConfigConstants.PENDING_INTENT_FLAGS
            )
        )
    }

    override fun getNextAlarmTime(alarms: List<Alarm>): Pair<Alarm, String>? {
        alarms
            .filter { it.triggerTime != null }
            .sortedBy { alarm -> alarm.triggerTime }
            .firstOrNull()
            ?.let { nextAlarm ->
                val timeString =
                    TimeUntilAlarm(nextAlarm.triggerTime!!).getTimeUntil() ?: run { return null }

                return nextAlarm to timeString
            } ?: return null
    }

    override fun setAlarmDataCallback(callback: AlarmDataCallback?) {
        this.dataCallback = callback
    }

    private fun schedule(timeInMillis: Long, alarm: Alarm, intent: Intent) {
        updateAlarm(alarm, timeInMillis)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            ConfigConstants.PENDING_INTENT_FLAGS
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
    }

    private fun getNextAvailableDay(calendar: Calendar, repeatDays: List<DayOfWeek>): Int {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

        if (repeatDays.isEmpty() && calendar.timeInMillis <= System.currentTimeMillis()) {
            return 1
        }

        if (repeatDays.isEmpty()) {
            return 0
        }

        val currentDayOfWeek = DayOfWeek.values().first { it.ordinal + 1 == today }

        val sortedDays = repeatDays.sortedBy { it.ordinal }

        for (day in sortedDays) {
            val daysDifference = day.ordinal - currentDayOfWeek.ordinal
            if (daysDifference > 0 || (daysDifference == 0 && calendar.timeInMillis > System.currentTimeMillis())) {
                return daysDifference
            }
        }

        return sortedDays.first().ordinal + 7 - currentDayOfWeek.ordinal
    }

    private fun getTimeLeftUntilAlarm(timeInMillis: Long): String? {
        return TimeUntilAlarm(timeInMillis).getTimeUntil()
    }

    private fun getDefaultIntent(alarmId: Long) =
        Intent(context, AlarmBroadcastReceiver::class.java).apply {
            putExtra(IntentExtra.ID_EXTRA, alarmId)
        }

    private fun getTimeInstance(alarm: Alarm): Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            if (alarm.meridiem == null) {
                set(
                    Calendar.HOUR_OF_DAY,
                    alarm.hour.let {
                        if (it == 12) 0 else it
                    })
            } else {
                set(Calendar.HOUR, alarm.hour)
                set(Calendar.AM_PM, alarm.meridiem.ordinal)
            }
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
        }
    }

    private fun updateAlarm(alarm: Alarm, timeInMillis: Long?) {
        dataCallback?.onAlarmUpdated(alarm.apply { triggerTime = timeInMillis })
    }
}

