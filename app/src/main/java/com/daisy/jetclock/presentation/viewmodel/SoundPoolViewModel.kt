package com.daisy.jetclock.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.daisy.jetclock.domain.model.SoundOption
import com.daisy.jetclock.utils.SoundPoolManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SoundPoolViewModel @Inject constructor(
    private val soundPoolManager: SoundPoolManager,
) : ViewModel() {

    fun playSound(context: Context, sound: String) {
        val soundAsset = context.assets.openFd(SoundOption.getAssetFilename(sound))
        soundPoolManager.changeSound(soundAsset)
    }

    fun stopSound() {
        soundPoolManager.stopSound()
    }

    override fun onCleared() {
        super.onCleared()
        soundPoolManager.release()
    }
}