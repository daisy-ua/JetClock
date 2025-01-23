package com.daisy.jetclock.domain.repository

import com.daisy.jetclock.domain.model.TimeFormat
import kotlinx.coroutines.flow.Flow

interface UISettingsRepository {

    val timeFormat: Flow<TimeFormat>

    suspend fun setTimeFormat(timeFormat: TimeFormat)
}