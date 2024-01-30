package com.daisy.jetclock.viewmodels

import androidx.lifecycle.ViewModel
import com.daisy.jetclock.domain.SoundOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SelectedSoundViewModel @Inject constructor() : ViewModel() {
    private val _selectedSound: MutableStateFlow<SoundOption> = MutableStateFlow(SoundOption())
    val selectedSound: StateFlow<SoundOption> get() = _selectedSound

    fun updateSelectedSound(soundFile: String?) {
        _selectedSound.value = SoundOption(soundFile)
    }
}