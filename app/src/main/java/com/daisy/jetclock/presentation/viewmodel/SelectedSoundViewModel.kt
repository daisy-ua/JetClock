package com.daisy.jetclock.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.daisy.jetclock.domain.model.SoundOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SelectedSoundViewModel @Inject constructor() : ViewModel() {
    private val _selectedSound: MutableStateFlow<SoundOption> = MutableStateFlow(SoundOption())
    val selectedSound: StateFlow<SoundOption> get() = _selectedSound

    fun updateSelectedSound(sound: SoundOption) {
        _selectedSound.value = sound
    }
}