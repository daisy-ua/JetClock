package com.daisy.jetclock.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisy.jetclock.domain.Alarm
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

    fun enableAlarm(alarm: Alarm) = viewModelScope.launch {
        repository.insertAlarm(alarm)
    }

    fun deleteAlarm(id: Long) = viewModelScope.launch {
        repository.deleteAlarm(id)
    }
}