package com.daisy.jetclock.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.usecase.CancelAlarmUseCase
import com.daisy.jetclock.domain.usecase.DeleteAlarmUseCase
import com.daisy.jetclock.domain.usecase.GetAlarmsUseCase
import com.daisy.jetclock.domain.usecase.ScheduleAlarmUseCase
import com.daisy.jetclock.presentation.utils.next.NextAlarmHandler
import com.daisy.jetclock.presentation.utils.next.TimeUntilNextAlarm
import com.daisy.jetclock.presentation.utils.next.getTimeLeftUntilAlarm
import com.daisy.jetclock.utils.toast.ToastStateHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    getAlarmsUseCase: GetAlarmsUseCase,
    private val scheduleAlarmUseCase: ScheduleAlarmUseCase,
    private val cancelAlarmUseCase: CancelAlarmUseCase,
    private val deleteAlarmUseCase: DeleteAlarmUseCase,
    private val nextAlarmHandler: NextAlarmHandler,
    val toastStateHandler: ToastStateHandler,
) : ViewModel() {
    val alarms = getAlarmsUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val nextAlarm: StateFlow<Alarm?> get() = nextAlarmHandler.nextAlarm
    val nextAlarmTime: StateFlow<TimeUntilNextAlarm?> get() = nextAlarmHandler.nextAlarmTime

    private var refreshJob: Job? = null

    init {
        viewModelScope.launch {
            alarms.collectLatest { alarmList ->
                nextAlarmHandler.updateNextAlarm(alarmList)
            }
        }
    }

    fun startRefreshingNextAlarmTime() {
        if (refreshJob?.isActive == true) return

        refreshJob = viewModelScope.launch {
            while (true) {
                nextAlarmHandler.updateNextAlarmTime()
                val now = System.currentTimeMillis()
                val delayUntilNextMinute = 60000 - (now % 60000)
                delay(delayUntilNextMinute)
            }
        }
    }

    fun stopRefreshingNextAlarmTime() {
        refreshJob?.cancel()
    }

    fun changeCheckedState(alarm: Alarm) = viewModelScope.launch {
        scheduleAlarm(alarm)
        nextAlarmHandler.updateNextAlarm(alarms.value)
    }

    fun deleteAlarm(alarm: Alarm) = viewModelScope.launch {
        deleteAlarmUseCase(alarm)
    }

    private suspend fun scheduleAlarm(alarm: Alarm) {
        if (alarm.isEnabled) {
            cancelAlarmUseCase(alarm)
            toastStateHandler.clearToastMessage()
        } else {
            val timeInMillis = scheduleAlarmUseCase(alarm)
            toastStateHandler.setToastMessage(getTimeLeftUntilAlarm(timeInMillis))
        }
    }
}