package com.daisy.jetclock.core.media

import com.daisy.jetclock.domain.model.SoundOption
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaPlaybackServiceImpl @Inject constructor(
    private val mediaPlayerManager: MediaPlayerManager,
) : PlaybackService {
    override fun startPlayback(sound: SoundOption) {
        mediaPlayerManager.prepare(sound, true)
        mediaPlayerManager.start()
    }

    override fun stopPlayback() {
        mediaPlayerManager.release()
    }
}