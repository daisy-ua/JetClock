package com.daisy.jetclock.core.media

import com.daisy.jetclock.domain.model.SoundOption

interface PlaybackService {

    fun startPlayback(sound: SoundOption)

    fun stopPlayback()
}