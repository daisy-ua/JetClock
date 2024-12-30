package com.daisy.jetclock.domain.usecase

import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAlarmDetailsUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository,
) {
    operator fun invoke(id: Long): Flow<Alarm?> {
        return alarmRepository.getAlarmById(id)
    }
}