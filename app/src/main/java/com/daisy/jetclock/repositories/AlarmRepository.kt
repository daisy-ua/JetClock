package com.daisy.jetclock.repositories

import com.daisy.jetclock.domain.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun getAllAlarms(): Flow<List<Alarm>>

    fun getAlarmById(id: Long): Flow<Alarm?>

    suspend fun insertAlarm(alarm: Alarm): Long

    suspend fun deleteAlarm(id: Long)
}