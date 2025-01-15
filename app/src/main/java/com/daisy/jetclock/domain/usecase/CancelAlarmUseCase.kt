package com.daisy.jetclock.domain.usecase

import com.daisy.jetclock.core.receiver.AlarmBroadcastReceiver
import com.daisy.jetclock.core.receiver.ServiceHandler
import com.daisy.jetclock.core.scheduler.AlarmSchedulerManager
import com.daisy.jetclock.core.utils.AlarmStateUpdater
import com.daisy.jetclock.domain.model.Alarm
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CancelAlarmUseCase @Inject constructor(
    private val stateUpdater: AlarmStateUpdater,
    private val alarmSchedulerManager: AlarmSchedulerManager,
    private val serviceHandler: ServiceHandler<Long>,
) {
    suspend operator fun invoke(alarm: Alarm) {
        serviceHandler.start(alarm.id, AlarmBroadcastReceiver.ACTION_CANCEL)

        stateUpdater.cancelAlarm(alarm)

        alarmSchedulerManager.cancel(alarm)
    }
}