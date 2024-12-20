package com.daisy.jetclock.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.daisy.jetclock.data.local.converter.DateTimeConverter
import com.daisy.jetclock.data.local.converter.DayOfWeekConverter
import com.daisy.jetclock.data.local.dao.AlarmDao
import com.daisy.jetclock.data.local.entity.AlarmEntity

@Database(
    entities = [AlarmEntity::class],
    version = 1
)
@TypeConverters(DayOfWeekConverter::class, DateTimeConverter::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
}