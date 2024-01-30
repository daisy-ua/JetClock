package com.daisy.jetclock.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.daisy.jetclock.domain.SoundOption
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