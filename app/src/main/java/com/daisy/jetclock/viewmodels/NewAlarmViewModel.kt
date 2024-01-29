package com.daisy.jetclock.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisy.jetclock.constants.MeridiemOption
import com.daisy.jetclock.constants.NewAlarmDefaults
import com.daisy.jetclock.domain.Alarm
import com.daisy.jetclock.domain.RepeatDays
import com.daisy.jetclock.domain.RingDurationOption
import com.daisy.jetclock.domain.SnoozeOption
import com.daisy.jetclock.domain.TimeOfDay
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

    private val _repeatDays: MutableStateFlow<RepeatDays> = MutableStateFlow(
        RepeatDays(_alarm.value.repeatDays)
    )
    val repeatDays: StateFlow<RepeatDays> get() = _repeatDays

    fun updateRepeatDays(newRepeatDays: RepeatDays) {
        _repeatDays.value = newRepeatDays
    }

    private val _time: MutableStateFlow<TimeOfDay> = MutableStateFlow(
        _alarm.value.let {
            TimeOfDay(it.hour, it.minute, it.meridiem)
        }
    )
    val time: StateFlow<TimeOfDay> get() = _time

    fun updateTime(hour: Int, minute: Int, meridiem: MeridiemOption?) {
        viewModelScope.launch {
            _time.value = TimeOfDay(hour, minute, meridiem)
        }
    }

    private var isSaving: Boolean = false

    init {
        viewModelScope.launch {
            _alarm.collect { updatedAlarm ->
                updateLabel(updatedAlarm.label)
                updateRingDuration(RingDurationOption(updatedAlarm.ringDuration))
                updateSnoozeDuration(
                    SnoozeOption(
                        updatedAlarm.snoozeDuration,
                        updatedAlarm.snoozeNumber
                    )
                )
                updateRepeatDays(RepeatDays(updatedAlarm.repeatDays))
                updateTime(updatedAlarm.hour, updatedAlarm.minute, updatedAlarm.meridiem)
            }
        }
    }

    fun getAlarmById(id: Long) = viewModelScope.launch {
        if (id == NewAlarmDefaults.NEW_ALARM_ID) return@launch
        repository.getAlarmById(id).collect {
            it?.let { _alarm.value = it }
        }
    }

    fun saveAlarm(callback: () -> Unit) = viewModelScope.launch {
        val updatedAlarm = getUpdatedAlarm()
        if (!isSaving) {
            isSaving = true
            repository.insertAlarm(updatedAlarm)
            isSaving = false
            delay(100L)
        }
        callback.invoke()
    }

    fun deleteAlarm(id: Long, callback: () -> Unit) = viewModelScope.launch {
        repository.deleteAlarm(id)
        delay(100L)
        callback.invoke()
    }

    private fun getUpdatedAlarm(): Alarm = Alarm(
        id = _alarm.value.id,
        hour = time.value.hour,
        minute = time.value.minute,
        meridiem = time.value.meridiem,
        repeatDays = repeatDays.value.days,
        isEnabled = true,
        label = label.value,
        ringDuration = ringDuration.value.value,
        snoozeDuration = snoozeDuration.value.duration,
        snoozeNumber = snoozeDuration.value.number,
        sound = "soundFile.value"
    )
}