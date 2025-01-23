package com.daisy.jetclock.data.repository

import com.daisy.jetclock.data.local.dao.AlarmDao
import com.daisy.jetclock.data.mapper.convertToDomain
import com.daisy.jetclock.data.mapper.convertToEntity
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.repository.AlarmRepository
import com.daisy.jetclock.domain.usecase.GetTimeFormatUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao,
    private val getTimeFormatUseCase: GetTimeFormatUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : AlarmRepository {

    override fun getAllAlarms(): Flow<List<Alarm>> {
        return combine(
            alarmDao.getAllAlarms(),
            getTimeFormatUseCase()
        ) { alarms, timeFormat ->
            alarms.map { it.convertToDomain(timeFormat) }
        }
    }

    override fun getAlarmById(id: Long): Flow<Alarm?> {
        return combine(
            alarmDao.getAlarm(id),
            getTimeFormatUseCase()
        ) { alarm, timeFormat ->
            alarm?.convertToDomain(timeFormat)
        }
    }

    override suspend fun insertAlarm(alarm: Alarm): Long {
        return withContext(ioDispatcher) {
            alarm.convertToEntity().let { entity ->
                alarmDao.insertAlarm(entity)
            }
        }
    }

    override suspend fun deleteAlarm(id: Long) {
        withContext(ioDispatcher) {
            alarmDao.deleteAlarm(id)
        }
    }
}