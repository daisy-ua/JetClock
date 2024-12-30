package com.daisy.jetclock.domain.usecase

import com.daisy.jetclock.core.scheduler.AlarmSchedulerManager
import com.daisy.jetclock.core.receiver.ServiceHandler
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.repository.AlarmRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteAlarmUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmSchedulerManager: AlarmSchedulerManager,
    private val mediaPlaybackHandler: ServiceHandler<Long>,
) {
    suspend operator fun invoke(alarm: Alarm) {
        mediaPlaybackHandler.stop(alarm.id)

        alarmRepository.deleteAlarm(alarm.id)
//        TODO: should i check if alarm is currently triggered?
        alarmSchedulerManager.cancel(alarm)
    }
}