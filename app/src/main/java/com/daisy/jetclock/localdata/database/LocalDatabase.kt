package com.daisy.jetclock.localdata.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.daisy.jetclock.localdata.converters.DayOfWeekConverter
import com.daisy.jetclock.localdata.dao.AlarmDao
import com.daisy.jetclock.localdata.entities.AlarmEntity

@Database(
    entities = [AlarmEntity::class],
    version = 1
)
@TypeConverters(DayOfWeekConverter::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
}