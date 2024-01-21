package com.daisy.jetclock.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisy.jetclock.domain.Alarm
import com.daisy.jetclock.repositories.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewAlarmViewModel @Inject constructor(
    private val repository: AlarmRepository,
) : ViewModel() {

    fun insertAlarm(alarm: Alarm) = viewModelScope.launch {
        repository.insertAlarm(alarm)
    }
}