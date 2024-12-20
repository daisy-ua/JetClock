package com.daisy.jetclock.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisy.jetclock.core.manager.AlarmActionManager
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.repository.AlarmRepository
import com.daisy.jetclock.utils.AlarmDataCallback
import com.daisy.jetclock.utils.nextalarm.NextAlarmHandler
import com.daisy.jetclock.utils.nextalarm.getTimeLeftUntilAlarm
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
    private val repository: AlarmRepository,
    private val alarmActionManager: AlarmActionManager,
    private val nextAlarmHandler: NextAlarmHandler,
    val toastStateHandler: ToastStateHandler,
) : ViewModel(), AlarmDataCallback {
    val alarms = repository.getAllAlarms()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val nextAlarm: StateFlow<Alarm?> get() = nextAlarmHandler.nextAlarm
    val nextAlarmTime: StateFlow<String?> get() = nextAlarmHandler.nextAlarmTime

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

    override fun onAlarmUpdated(alarm: Alarm) {
        viewModelScope.launch {
            repository.insertAlarm(alarm)
        }
    }

    fun changeCheckedState(alarm: Alarm) = viewModelScope.launch {
        scheduleAlarm(alarm)
        nextAlarmHandler.updateNextAlarm(alarms.value)
    }

    fun deleteAlarm(alarm: Alarm) = viewModelScope.launch {
        alarmActionManager.delete(alarm)
    }

    private suspend fun scheduleAlarm(alarm: Alarm) {
        if (alarm.isEnabled) {
            alarmActionManager.cancel(alarm)
            toastStateHandler.clearToastMessage()
        } else {
            val timeInMillis = alarmActionManager.schedule(alarm)
            toastStateHandler.setToastMessage(getTimeLeftUntilAlarm(timeInMillis))
        }
    }
}