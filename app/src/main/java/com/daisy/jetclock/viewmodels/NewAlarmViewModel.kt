package com.daisy.jetclock.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisy.jetclock.constants.NewAlarmDefaults
import com.daisy.jetclock.domain.Alarm
import com.daisy.jetclock.domain.RingDurationOption
import com.daisy.jetclock.domain.SnoozeOption
import com.daisy.jetclock.repositories.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewAlarmViewModel @Inject constructor(
    private val repository: AlarmRepository,
) : ViewModel() {
    private val _alarm: MutableStateFlow<Alarm> = MutableStateFlow(NewAlarmDefaults.NEW_ALARM)

    private val _label: MutableStateFlow<String> = MutableStateFlow(_alarm.value.label)
    val label: StateFlow<String> get() = _label

    fun updateLabel(newLabel: String) {
        _label.value = newLabel
    }

    private val _ringDuration: MutableStateFlow<RingDurationOption> = MutableStateFlow(
        RingDurationOption(_alarm.value.ringDuration)
    )
    val ringDuration: StateFlow<RingDurationOption> get() = _ringDuration

    fun updateRingDuration(newRingDurationOption: RingDurationOption) {
        _ringDuration.value = newRingDurationOption
    }

    private val _snoozeDuration: MutableStateFlow<SnoozeOption> = MutableStateFlow(
        SnoozeOption(_alarm.value.snoozeDuration, _alarm.value.snoozeNumber)
    )
    val snoozeDuration: StateFlow<SnoozeOption> get() = _snoozeDuration

    fun updateSnoozeDuration(newSnoozeDurationOption: SnoozeOption) {
        _snoozeDuration.value = newSnoozeDurationOption
    }

    private var isSaving: Boolean = false

    init {
        Log.d("daisy-ua", "set vm init")

        viewModelScope.launch {
            Log.d("daisy-ua", "alarm changed")
            _alarm.collect { updatedAlarm ->
                _label.value = updatedAlarm.label
                _ringDuration.value = RingDurationOption(updatedAlarm.ringDuration)
                _snoozeDuration.value =
                    SnoozeOption(updatedAlarm.snoozeDuration, updatedAlarm.snoozeNumber)
            }
        }
    }

    fun getAlarmById(id: Long) = viewModelScope.launch {
        if (id == NewAlarmDefaults.NEW_ALARM_ID) return@launch
        repository.getAlarmById(id).collect {
            _alarm.value = it
        }
    }

    fun saveAlarm(callback: () -> Unit) {
        val updatedAlarm = Alarm(
            id = _alarm.value.id,
            hour = 1,
            minute = 10,
            meridiem = null,
            repeatDays = emptyList(),
            isEnabled = true,
            label = label.value,
            ringDuration = ringDuration.value.value,
            snoozeDuration = snoozeDuration.value.duration,
            snoozeNumber = snoozeDuration.value.number,
            sound = "soundFile.value"
        )

        viewModelScope.launch {
            if (!isSaving) {
                isSaving = true
                repository.insertAlarm(updatedAlarm)
                delay(50L)
            }
            callback.invoke()
        }
    }

    fun deleteAlarm(id: Long) = viewModelScope.launch {
        repository.deleteAlarm(id)
    }
}