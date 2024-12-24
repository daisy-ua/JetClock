package com.daisy.jetclock.domain.repository

import android.content.res.AssetFileDescriptor
import com.daisy.jetclock.domain.model.SoundOption
import kotlinx.coroutines.flow.Flow

interface SoundRepository {

    fun getSoundOptions(path: String): Flow<List<SoundOption>>

    fun getAfd(path: String, soundFile: String): AssetFileDescriptor?
}