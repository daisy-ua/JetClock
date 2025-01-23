package com.daisy.jetclock.domain.usecase

import com.daisy.jetclock.domain.model.TimeFormat
import com.daisy.jetclock.domain.repository.UISettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetTimeFormatUseCase @Inject constructor(
    private val repository: UISettingsRepository,
) {
    operator fun invoke(): Flow<TimeFormat> = repository.timeFormat
}