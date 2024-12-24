package com.daisy.jetclock.presentation.viewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisy.jetclock.constants.ConfigConstants
import com.daisy.jetclock.domain.model.SoundOption
import com.daisy.jetclock.domain.repository.SoundRepository
import com.daisy.jetclock.utils.SoundPoolManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SoundSelectionViewModel @Inject constructor(
    private val soundRepository: SoundRepository,
    private val soundPoolManager: SoundPoolManager,
) : ViewModel(), DefaultLifecycleObserver {
    val sounds = soundRepository.getSoundOptions(ConfigConstants.SOUND_ASSETS_DIR)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _selectedSoundOption: MutableStateFlow<SoundOption> =
        MutableStateFlow(SoundOption.none)
    val selectedSoundOption: StateFlow<SoundOption> get() = _selectedSoundOption

    fun updateSelectedSoundOption(newSound: SoundOption) {
        _selectedSoundOption.value = newSound
    }

    fun onSoundClicked(soundOption: SoundOption) {
        updateSelectedSoundOption(soundOption)
        soundOption.soundFile?.let { filename ->
            playSound(filename)
        } ?: stopSound()
    }

    fun attachLifecycle(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        stopSound()
    }

    override fun onCleared() {
        super.onCleared()
        soundPoolManager.release()
    }

    private fun playSound(filename: String) {
        val soundAsset = soundRepository.getAfd(ConfigConstants.SOUND_ASSETS_DIR, filename)
        soundAsset?.use { afd ->
            soundPoolManager.changeSound(afd)
        }
    }

    private fun stopSound() {
        soundPoolManager.stopSound()
    }
}