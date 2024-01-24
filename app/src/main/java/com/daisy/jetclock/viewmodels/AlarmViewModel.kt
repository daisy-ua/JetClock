package com.daisy.jetclock.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisy.jetclock.domain.Alarm
import com.daisy.jetclock.domain.DayOfWeek
import com.daisy.jetclock.repositories.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val repository: AlarmRepository,
) : ViewModel() {
    val alarms = repository.getAllAlarms()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun changeCheckedState(alarm: Alarm, isEnabled: Boolean) = viewModelScope.launch {
        repository.insertAlarm(alarm.also { it.isEnabled = isEnabled })
    }

    fun deleteAlarm(id: Long) = viewModelScope.launch {
        repository.deleteAlarm(id)
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
}