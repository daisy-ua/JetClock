package com.daisy.jetclock.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisy.jetclock.constants.MeridiemOption
import com.daisy.jetclock.constants.NewAlarmDefaults
import com.daisy.jetclock.core.manager.AlarmActionManager
import com.daisy.jetclock.domain.Alarm
import com.daisy.jetclock.domain.RepeatDays
import com.daisy.jetclock.domain.RingDurationOption
import com.daisy.jetclock.domain.SnoozeOption
import com.daisy.jetclock.domain.TimeOfDay
import com.daisy.jetclock.repositories.AlarmRepository
import com.daisy.jetclock.utils.ToastManager
import com.daisy.jetclock.utils.getTimeLeftUntilAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class NewAlarmViewModel @Inject constructor(
    private val repository: AlarmRepository,
    private val alarmActionManager: AlarmActionManager,
    val toastManager: ToastManager,
) : ViewModel() {
    private val _alarm: MutableStateFlow<Alarm> = MutableStateFlow(NewAlarmDefaults.getNewAlarm())

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

    private fun updateTime(hour: Int, minute: Int, meridiem: MeridiemOption?) {
        viewModelScope.launch {
            _time.value = TimeOfDay(hour, minute, meridiem)
        }
    }

    private var selectedTime = _alarm.value.let { alarm ->
        TimeOfDay(alarm.hour, alarm.minute, alarm.meridiem)
    }

    fun updateSelectedTime(hour: Int, minute: Int, meridiem: MeridiemOption?) {
        selectedTime = TimeOfDay(hour, minute, meridiem)
    }

    fun syncTime() {
        _time.value = selectedTime
    }

    private val _soundFileName: MutableStateFlow<String?> = MutableStateFlow(_alarm.value.sound)

    fun updateSoundFile(newSound: String?) {
        _soundFileName.value = newSound
    }

    private var updateSoundViewModelCallback: ((String?) -> Unit)? = null

    fun setUpdateSoundViewModelCallback(callback: (String?) -> Unit) {
        updateSoundViewModelCallback = callback
    }

    private var isSaving: AtomicBoolean = AtomicBoolean(false)

    private val _toastMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

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
                updateSelectedTime(updatedAlarm.hour, updatedAlarm.minute, updatedAlarm.meridiem)
                updateSoundFile(updatedAlarm.sound)
                updateSoundViewModelCallback?.invoke(updatedAlarm.sound)
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
        if (isSaving.compareAndSet(false, true)) {
            try {
                val timeInMillis = alarmActionManager.reschedule(updatedAlarm, _alarm.value)
                _toastMessage.value = getTimeLeftUntilAlarm(timeInMillis)
                delay(100L)
            } finally {
                isSaving.set(false)
                callback.invoke()
            }
        }
    }

    fun deleteAlarm(callback: () -> Unit) = viewModelScope.launch {
        alarmActionManager.delete(_alarm.value)
        delay(100L)
        callback.invoke()
    }

    private fun getUpdatedAlarm(): Alarm = Alarm(
        id = _alarm.value.id,
        hour = selectedTime.hour,
        minute = selectedTime.minute,
        meridiem = selectedTime.meridiem,
        repeatDays = repeatDays.value.days,
        isEnabled = true,
        triggerTime = null,
        label = label.value,
        ringDuration = ringDuration.value.value,
        snoozeDuration = snoozeDuration.value.duration,
        snoozeNumber = snoozeDuration.value.number,
        snoozeCount = _alarm.value.snoozeCount,
        sound = _soundFileName.value
    )
}