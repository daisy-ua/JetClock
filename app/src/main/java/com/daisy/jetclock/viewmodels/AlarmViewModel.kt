package com.daisy.jetclock.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisy.jetclock.core.manager.AlarmController
import com.daisy.jetclock.domain.Alarm
import com.daisy.jetclock.domain.DayOfWeek
import com.daisy.jetclock.domain.TimeUntilAlarm
import com.daisy.jetclock.repositories.AlarmRepository
import com.daisy.jetclock.ui.component.timepicker.TimeFormatter
import com.daisy.jetclock.utils.AlarmDataCallback
import com.daisy.jetclock.utils.ToastManager
import com.daisy.jetclock.utils.getNextAlarmTime
import com.daisy.jetclock.utils.getTimeLeftUntilAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val repository: AlarmRepository,
    private val alarmController: AlarmController,
    val toastManager: ToastManager,
) : ViewModel(), AlarmDataCallback {
    val alarms = repository.getAllAlarms()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _toastMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val _nextAlarm: MutableStateFlow<Alarm?> = MutableStateFlow(null)
    val nextAlarm: StateFlow<Alarm?> get() = _nextAlarm

    private val _nextAlarmTime: MutableStateFlow<String?> = MutableStateFlow(null)
    val nextAlarmTime: StateFlow<String?> get() = _nextAlarmTime

    private var refreshJob: Job? = null

    init {
        alarmController.setAlarmDataCallback(this)

        viewModelScope.launch {
            alarms.collectLatest { alarmList ->
                updateNextAlarm(alarmList)
            }
        }
    }

    private fun updateNextAlarm(alarmList: List<Alarm>) {
        val (nextAlarm, nextAlarmTime) = getNextAlarmTime(alarmList)
            ?: Pair(
                null,
                null
            )

        _nextAlarm.value = nextAlarm
        _nextAlarmTime.value = nextAlarmTime
    }

    fun changeCheckedState(alarm: Alarm, isEnabled: Boolean) = viewModelScope.launch {
        scheduleAlarm(alarm)
        repository.insertAlarm(alarm.also { it.isEnabled = isEnabled })
        updateNextAlarm(alarms.value)
    }

    fun deleteAlarm(alarm: Alarm) = viewModelScope.launch {
        repository.deleteAlarm(alarm.id)

        alarmController.delete(alarm)
    }

    fun getTimeString(hour: Int, minute: Int): String {
        return "$hour:${TimeFormatter.convertTwoCharTime(minute)}"
    }

    fun getRepeatDaysString(repeatDays: List<DayOfWeek>): String {
        return when {
            repeatDays.isEmpty() -> "Ring only once"

            repeatDays.size == 7 -> "Everyday"

            repeatDays.size == 5 && !repeatDays.containsAll(
                listOf(
                    DayOfWeek.SATURDAY,
                    DayOfWeek.SUNDAY
                )
            ) -> "Monday to Friday"

            else -> repeatDays.joinToString { day -> day.abbr }
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun startRefreshingNextAlarmTime() {
        if (refreshJob?.isActive == true) return

        refreshJob = viewModelScope.launch {
            while (true) {
                updateNextAlarmTime()
                val now = System.currentTimeMillis()
                val delayUntilNextMinute = 60000 - (now % 60000)
                delay(delayUntilNextMinute)
            }
        }
    }

    fun stopRefreshingNextAlarmTime() {
        refreshJob?.cancel()
    }

    override fun onAlarmUpdated(alarm: Alarm) {
        viewModelScope.launch {
            repository.insertAlarm(alarm)
        }
    }

    private fun scheduleAlarm(alarm: Alarm) {
        if (alarm.isEnabled) {
            alarmController.cancel(alarm)
            clearToastMessage()
        } else {
            val timeInMillis = alarmController.schedule(alarm)
            _toastMessage.value = getTimeLeftUntilAlarm(timeInMillis)
        }
    }

    private fun updateNextAlarmTime() {
        val time = nextAlarm.value?.triggerTime
        time?.let {
            _nextAlarmTime.value = TimeUntilAlarm(it).getTimeUntil()
        }
    }
}