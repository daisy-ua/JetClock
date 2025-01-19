package com.daisy.jetclock.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisy.jetclock.constants.DefaultAlarmConfig
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.model.RepeatDays
import com.daisy.jetclock.domain.model.RingDurationOption
import com.daisy.jetclock.domain.model.SnoozeOption
import com.daisy.jetclock.domain.model.SoundOption
import com.daisy.jetclock.domain.model.TimeOfDay
import com.daisy.jetclock.domain.usecase.DeleteAlarmUseCase
import com.daisy.jetclock.domain.usecase.GetAlarmDetailsUseCase
import com.daisy.jetclock.domain.usecase.ScheduleAlarmUseCase
import com.daisy.jetclock.presentation.navigation.MainDestinations
import com.daisy.jetclock.presentation.utils.next.getTimeLeftUntilAlarm
import com.daisy.jetclock.utils.toast.ToastStateHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class AlarmDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    alarmConfig: DefaultAlarmConfig,
    private val getAlarmDetailsUseCase: GetAlarmDetailsUseCase,
    private val scheduleAlarmUseCase: ScheduleAlarmUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
    val toastStateHandler: ToastStateHandler,
) : ViewModel() {
    private val _alarm: MutableStateFlow<Alarm> = MutableStateFlow(alarmConfig.defaultAlarm)
    val alarm: StateFlow<Alarm> get() = _alarm

    private val soundFileFlow: MutableSharedFlow<String> = MutableSharedFlow(replay = 1)

    init {
        initAlarm(savedStateHandle)

        observeSoundFile()
    }

    private fun initAlarm(savedStateHandle: SavedStateHandle) {
        val alarmId = savedStateHandle.get<Long>(MainDestinations.ALARM_ID_KEY.name)

        alarmId?.let { id ->
            viewModelScope.launch {
                getAlarmDetailsUseCase(id).collect {
                    it?.let {
                        _alarm.value = it
                    }
                }
            }
        }
    }

    private fun observeSoundFile() = viewModelScope.launch {
        soundFileFlow
            .distinctUntilChanged()
            .collect { soundFile ->
                updateSoundFile(SoundOption(soundFile))
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

    fun emitSoundFile(newSoundFile: String) {
        soundFileFlow.tryEmit(newSoundFile)
    }

    private fun updateSoundFile(newSound: SoundOption) {
        updateAlarm { copy(soundOption = newSound) }
    }

    private var isSaving: AtomicBoolean = AtomicBoolean(false)

    fun saveAlarm(callback: () -> Unit) = viewModelScope.launch {
        if (isSaving.compareAndSet(false, true)) {
            try {
                val timeInMillis = scheduleAlarmUseCase(_alarm.value)
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
        deleteAlarmUseCase(_alarm.value)
        delay(100L)
        callback.invoke()
    }

    private fun updateAlarm(update: Alarm.() -> Alarm) {
        _alarm.value = _alarm.value.update()
    }
}