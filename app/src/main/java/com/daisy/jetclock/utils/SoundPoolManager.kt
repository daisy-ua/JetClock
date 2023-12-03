package com.daisy.jetclock.utils

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.SoundPool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SoundPoolManager {
    private val soundPool = SoundPool.Builder().setMaxStreams(1).build()
    private var soundId: Int? = null
    private var streamId: Int? = null

    fun setSoundId(context: Context, soundCode: Int, priority: Int = 1) {
        soundId = soundPool.load(context, soundCode, priority)
    }

    fun changeSound(
        soundAfd: AssetFileDescriptor,
        leftVolume: Float = 1f,
        rightVolume: Float = 1f,
        priority: Int = 1,
        loop: Int = 0,
        rate: Float = 1f,
    ) {
        soundPool.setOnLoadCompleteListener { _, _, _ ->
            playSound(leftVolume, rightVolume, priority, loop, rate)
        }
        stopSound()
        soundId = soundPool.load(soundAfd, 1)
    }

    fun playSound(
        leftVolume: Float = 1f,
        rightVolume: Float = 1f,
        priority: Int = 1,
        loop: Int = 0,
        rate: Float = 1f,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            soundId?.let { soundId ->
                streamId = soundPool.play(soundId, leftVolume, rightVolume, priority, loop, rate)
            }
        }
    }

    fun stopSound() {
        streamId?.let { streamId -> soundPool.stop(streamId) }
    }

    fun release() {
        soundPool.release()
    }
}