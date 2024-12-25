package com.daisy.jetclock.domain.repository

import com.daisy.jetclock.domain.model.SoundOption
import kotlinx.coroutines.flow.Flow

interface SoundRepository {

    fun getSoundOptions(path: String): Flow<List<SoundOption>>
}