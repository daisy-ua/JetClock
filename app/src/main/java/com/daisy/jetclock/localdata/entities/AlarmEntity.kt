package com.daisy.jetclock.localdata.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.daisy.jetclock.domain.DayOfWeek
import java.time.LocalTime

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    val time: LocalTime,

    @ColumnInfo(name = "repeat_days")
    val repeatDays: List<DayOfWeek>,

    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean,

    val label: String,

    @ColumnInfo(name = "ring_duration")
    val ringDuration: Int,

    @ColumnInfo(name = "snooze_duration")
    val snoozeDuration: Int,

    @ColumnInfo(name = "snooze_number")
    val snoozeNumber: Int,

    val sound: String?,
)


