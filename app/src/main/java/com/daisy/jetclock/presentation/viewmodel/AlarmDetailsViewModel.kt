package com.daisy.jetclock.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisy.jetclock.constants.NewAlarmDefaults
import com.daisy.jetclock.core.manager.AlarmActionManager
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.model.RepeatDays
import com.daisy.jetclock.domain.model.RingDurationOption
import com.daisy.jetclock.domain.model.SnoozeOption
import com.daisy.jetclock.domain.model.SoundOption
import com.daisy.jetclock.domain.model.TimeOfDay
import com.daisy.jetclock.domain.repository.AlarmRepository
import com.daisy.jetclock.utils.nextalarm.getTimeLeftUntilAlarm
import com.daisy.jetclock.utils.toast.ToastStateHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class AlarmDetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AlarmRepository,
    private val alarmActionManager: AlarmActionManager,
    val toastStateHandler: ToastStateHandler,
) : ViewModel() {
    private val _alarm: MutableStateFlow<Alarm> =
        MutableStateFlow(NewAlarmDefaults.getNewAlarm(context))
    val alarm: StateFlow<Alarm> get() = _alarm

    fun updateScreenData(id: Long) {
        if (id == NewAlarmDefaults.NEW_ALARM_ID || _alarm.value.id == id) return
        viewModelScope.launch {
            repository.getAlarmById(id).collect {
                it?.let {
                    _alarm.value = it
                    updateSoundViewModelCallback?.invoke(it.soundOption)
                }
            }
        }
    }

    fun updateLabel(newLabel: String) {
        updateAlarm { copy(label = newLabel) }
    }

    fun updateRingDuration(newRingDurationOption: RingDurationOption) {
        updateAlarm { copy(ringDurationOption = newRingDurationOption) }
    }

    fun updateSnoozeDuration(newSnoozeDurationOption: SnoozeOption) {
        updateAlarm { copy(snoozeOption = newSnoozeDurationOption) }
    }

    fun updateRepeatDays(newRepeatDays: RepeatDays) {
        updateAlarm { copy(repeatDays = newRepeatDays) }
    }

    fun updateTime(newTimeOfDay: TimeOfDay) {
        updateAlarm { copy(time = newTimeOfDay) }
    }

    fun updateSoundFile(newSound: SoundOption) {
        updateAlarm { copy(soundOption = newSound) }
    }

    private var updateSoundViewModelCallback: ((SoundOption) -> Unit)? = null

    fun setUpdateSoundViewModelCallback(callback: (SoundOption) -> Unit) {
        updateSoundViewModelCallback = callback
    }

    private var isSaving: AtomicBoolean = AtomicBoolean(false)

    fun saveAlarm(callback: () -> Unit) = viewModelScope.launch {
        val alarmToSave = _alarm.value
        if (isSaving.compareAndSet(false, true)) {
            try {
                val timeInMillis = alarmActionManager.reschedule(alarmToSave, _alarm.value)
                toastStateHandler.clearToastMessage()
                toastStateHandler.setToastMessage(getTimeLeftUntilAlarm(timeInMillis))
                delay(100L)
            } catch (e: Exception) {
                Log.e("NewAlarmViewModel", "Error saving alarm", e)
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

    private fun updateAlarm(update: Alarm.() -> Alarm) {
        _alarm.value = _alarm.value.update()
    }
}