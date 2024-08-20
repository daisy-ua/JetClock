package com.daisy.jetclock.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.daisy.jetclock.domain.SoundOption
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject


class MediaPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : MediaPlayer.OnPreparedListener {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    fun prepare(sound: String?, vibration: Boolean) {
        sound?.let {
            try {
                context.assets.openFd(SoundOption.getAssetFn(sound)).use { soundAfd ->
                    mediaPlayer = MediaPlayer().apply {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .build()
                        )
                        isLooping = true
                        with(soundAfd) {
                            setDataSource(fileDescriptor, startOffset, length)
                        }
                        setOnPreparedListener(this@MediaPlayerManager)
                        prepareAsync()
                    }
                }
            } catch (e: IOException) {
                Log.e("daisy-ua", e.message.toString())
            }
        }

        if (vibration) {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
    }

    fun start() {
        vibrator?.let {
            val effect = VibrationEffect.createWaveform(
                longArrayOf(0, 100, 200, 300, 400),
                intArrayOf(0, 100, 255, 100, 0),
                0
            )
            it.vibrate(effect)
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null

        vibrator?.cancel()
        vibrator = null
    }
}