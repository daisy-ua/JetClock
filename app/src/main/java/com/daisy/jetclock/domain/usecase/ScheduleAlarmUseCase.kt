package com.daisy.jetclock.domain.usecase

import com.daisy.jetclock.constants.DefaultAlarmConfig
import com.daisy.jetclock.core.scheduler.AlarmSchedulerManager
import com.daisy.jetclock.core.utils.AlarmStateUpdater
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.repository.AlarmRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleAlarmUseCase @Inject constructor(
    private val repository: AlarmRepository,
    private val stateUpdater: AlarmStateUpdater,
    private val alarmSchedulerManager: AlarmSchedulerManager,
) {
    suspend operator fun invoke(alarm: Alarm): Long {
        val id = if (alarm.id == DefaultAlarmConfig.NEW_ALARM_ID) {
            repository.insertAlarm(alarm)
        } else {
            null
        }

        val newAlarm = id?.let { alarm.copy(id = it) } ?: alarm

        val timeInMillis = alarmSchedulerManager.schedule(newAlarm)
        stateUpdater.scheduleAlarm(newAlarm, timeInMillis)
        return timeInMillis
    }
}