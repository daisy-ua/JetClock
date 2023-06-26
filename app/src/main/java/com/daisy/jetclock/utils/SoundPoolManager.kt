package com.daisy.jetclock.utils

import android.content.Context
import android.media.SoundPool
import com.daisy.jetclock.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SoundPoolManager(context: Context) {
    private val soundPool = SoundPool.Builder().setMaxStreams(1).build()
    private val soundId = soundPool.load(context, R.raw.gate_latch_click_1924, 1)

    fun playSound() {
        CoroutineScope(Dispatchers.IO).launch {
            soundPool.play(soundId, 1f, 1f, 1, 0, 5f)
        }
    }

    fun release() {
        soundPool.release()
    }
}