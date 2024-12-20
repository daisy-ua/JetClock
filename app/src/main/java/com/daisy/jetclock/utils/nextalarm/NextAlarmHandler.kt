package com.daisy.jetclock.utils.nextalarm

import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.model.TimeUntilAlarm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Singleton

@Singleton
class NextAlarmHandler {
    private val _nextAlarm: MutableStateFlow<Alarm?> = MutableStateFlow(null)
    val nextAlarm: StateFlow<Alarm?> get() = _nextAlarm

    private val _nextAlarmTime: MutableStateFlow<String?> = MutableStateFlow(null)
    val nextAlarmTime: StateFlow<String?> get() = _nextAlarmTime

    fun updateNextAlarm(alarmList: List<Alarm>) {
        val (nextAlarm, nextAlarmTime) = getNextAlarmTime(alarmList) ?: Pair(null, null)
        _nextAlarm.value = nextAlarm
        _nextAlarmTime.value = nextAlarmTime
    }

    fun updateNextAlarmTime() {
        val time = nextAlarm.value?.triggerTime
        time?.let {
            _nextAlarmTime.value = TimeUntilAlarm(it).getTimeUntil()
        }
    }
}