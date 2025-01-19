package com.daisy.jetclock.presentation.utils.next

import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.presentation.utils.helper.TimeMillisUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Singleton

@Singleton
class NextAlarmHandler {
    private val _nextAlarm: MutableStateFlow<Alarm?> = MutableStateFlow(null)
    val nextAlarm: StateFlow<Alarm?> get() = _nextAlarm

    private val _nextAlarmTime: MutableStateFlow<TimeUntilNextAlarm?> = MutableStateFlow(null)
    val nextAlarmTime: StateFlow<TimeUntilNextAlarm?> get() = _nextAlarmTime

    fun updateNextAlarm(alarmList: List<Alarm>) {
        alarmList
            .filter { it.triggerTime != null }
            .sortedBy { alarm -> alarm.triggerTime }
            .firstOrNull()
            .let { nextAlarm ->
                _nextAlarm.value = nextAlarm
                _nextAlarmTime.value =
                    nextAlarm?.triggerTime?.let { TimeMillisUtils.convertTimeUntilAlarmGoesOff(it) }
            }
    }

    fun updateNextAlarmTime() {
        val time = nextAlarm.value?.triggerTime
        _nextAlarmTime.value = time?.let {
            TimeMillisUtils.convertTimeUntilAlarmGoesOff(it)
        }
    }
}