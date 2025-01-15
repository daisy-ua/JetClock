package com.daisy.jetclock.domain.usecase

import com.daisy.jetclock.core.receiver.AlarmBroadcastReceiver
import com.daisy.jetclock.core.receiver.ServiceHandler
import com.daisy.jetclock.domain.model.Alarm
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DismissAlarmUseCase @Inject constructor(
    private val serviceHandler: ServiceHandler<Long>,
) {
    operator fun invoke(alarm: Alarm) {
        serviceHandler.start(alarm.id, AlarmBroadcastReceiver.ACTION_DISMISS)
    }
}