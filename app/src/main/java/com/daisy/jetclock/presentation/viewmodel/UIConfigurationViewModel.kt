package com.daisy.jetclock.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisy.jetclock.constants.ConfigConstants
import com.daisy.jetclock.domain.model.TimeFormat
import com.daisy.jetclock.domain.usecase.GetTimeFormatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class UIConfigurationViewModel @Inject constructor(
    getTimeFormatUseCase: GetTimeFormatUseCase,
) : ViewModel() {

    val timeFormat: StateFlow<TimeFormat> = getTimeFormatUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, ConfigConstants.DEFAULT_TIME_FORMAT)
}