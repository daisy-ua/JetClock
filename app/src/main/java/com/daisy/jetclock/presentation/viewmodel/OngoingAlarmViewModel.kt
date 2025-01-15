package com.daisy.jetclock.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisy.jetclock.core.utils.IntentExtra
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.usecase.DismissAlarmUseCase
import com.daisy.jetclock.domain.usecase.GetAlarmDetailsUseCase
import com.daisy.jetclock.domain.usecase.SnoozeAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OngoingAlarmViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAlarmDetailsUseCase: GetAlarmDetailsUseCase,
    private val snoozeAlarmUseCase: SnoozeAlarmUseCase,
    private val dismissAlarmUseCase: DismissAlarmUseCase,
) : ViewModel() {
    private val _alarm: MutableStateFlow<Alarm?> = MutableStateFlow(null)
    val alarm: StateFlow<Alarm?> get() = _alarm

    init {
        initAlarm(savedStateHandle)
    }

    fun snoozeAlarm() {
        alarm.value?.let { alarm ->
            snoozeAlarmUseCase(alarm)
        }
    }

    fun dismissAlarm() {
        alarm.value?.let { alarm ->
            dismissAlarmUseCase(alarm)
        }
    }

    private fun initAlarm(savedStateHandle: SavedStateHandle) {
        val alarmId = savedStateHandle.get<Long>(IntentExtra.ID_EXTRA)

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
}