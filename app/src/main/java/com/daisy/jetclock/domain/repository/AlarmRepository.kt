package com.daisy.jetclock.domain.repository

import com.daisy.jetclock.domain.model.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun getAllAlarms(): Flow<List<Alarm>>

    fun getAlarmById(id: Long): Flow<Alarm?>

    suspend fun insertAlarm(alarm: Alarm): Long

    suspend fun deleteAlarm(id: Long)
}