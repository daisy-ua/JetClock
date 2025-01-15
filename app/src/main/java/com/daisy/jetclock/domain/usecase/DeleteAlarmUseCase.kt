package com.daisy.jetclock.domain.usecase

import com.daisy.jetclock.core.receiver.AlarmBroadcastReceiver
import com.daisy.jetclock.core.receiver.ServiceHandler
import com.daisy.jetclock.core.scheduler.AlarmSchedulerManager
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.repository.AlarmRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmSchedulerManager: AlarmSchedulerManager,
    private val serviceHandler: ServiceHandler<Long>,
) {
    suspend operator fun invoke(alarm: Alarm) {
        serviceHandler.start(alarm.id, AlarmBroadcastReceiver.ACTION_CANCEL)

        alarmRepository.deleteAlarm(alarm.id)

        alarmSchedulerManager.cancel(alarm)
    }
}