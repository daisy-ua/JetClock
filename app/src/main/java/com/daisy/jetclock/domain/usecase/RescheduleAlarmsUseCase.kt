package com.daisy.jetclock.domain.usecase

import com.daisy.jetclock.core.scheduler.AlarmSchedulerManager
import com.daisy.jetclock.core.utils.AlarmStateUpdater
import com.daisy.jetclock.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RescheduleAlarmsUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmSchedulerManager: AlarmSchedulerManager,
    private val alarmStateUpdater: AlarmStateUpdater,
) {
    suspend operator fun invoke() {
        val alarms = alarmRepository.getAllAlarms()
            .map { alarmList ->
                alarmList.filter { alarm -> alarm.isEnabled }
            }
            .firstOrNull { it.isNotEmpty() }

        alarms?.forEach { alarm ->
            val timeInMillis = alarmSchedulerManager.schedule(alarm)
            alarmStateUpdater.scheduleAlarm(alarm, timeInMillis)
        }
    }
}