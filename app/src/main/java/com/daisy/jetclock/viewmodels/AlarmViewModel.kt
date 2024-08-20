package com.daisy.jetclock.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisy.jetclock.core.manager.AlarmSchedulerManager
import com.daisy.jetclock.domain.Alarm
import com.daisy.jetclock.domain.DayOfWeek
import com.daisy.jetclock.repositories.AlarmRepository
import com.daisy.jetclock.ui.component.timepicker.TimeFormatter
import com.daisy.jetclock.utils.ToastManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val repository: AlarmRepository,
    private val alarmScheduler: AlarmSchedulerManager,
    val toastManager: ToastManager,
) : ViewModel() {
    val alarms = repository.getAllAlarms()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _toastMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    fun changeCheckedState(alarm: Alarm, isEnabled: Boolean) = viewModelScope.launch {
        scheduleAlarm(alarm)
        repository.insertAlarm(alarm.also { it.isEnabled = isEnabled })
    }

    fun deleteAlarm(id: Long) = viewModelScope.launch {
        repository.deleteAlarm(id)
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

    private fun scheduleAlarm(alarm: Alarm) {
        if (alarm.isEnabled) {
            alarmScheduler.cancel(alarm.id)
            clearToastMessage()
        } else {
            _toastMessage.value = alarmScheduler.schedule(alarm)
        }
    }
}