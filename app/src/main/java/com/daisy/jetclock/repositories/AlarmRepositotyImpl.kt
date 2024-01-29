package com.daisy.jetclock.repositories

import com.daisy.jetclock.domain.Alarm
import com.daisy.jetclock.localdata.dao.AlarmDao
import com.daisy.jetclock.mappers.convertToDomain
import com.daisy.jetclock.mappers.convertToEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : AlarmRepository {
    override fun getAllAlarms(): Flow<List<Alarm>> {
        return alarmDao.getAllAlarms().map { alarms -> alarms.map { it.convertToDomain() } }
    }

    override fun getAlarmById(id: Long): Flow<Alarm?> {
        return alarmDao.getAlarm(id).map { it?.convertToDomain() }
    }

    override suspend fun insertAlarm(alarm: Alarm) {
        withContext(ioDispatcher) {
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