package com.daisy.jetclock.data.repository

import com.daisy.jetclock.data.source.AssetDataSource
import com.daisy.jetclock.domain.model.SoundOption
import com.daisy.jetclock.domain.repository.SoundRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SoundRepositoryImpl @Inject constructor(
    private val assetDataSource: AssetDataSource,
) : SoundRepository {

    override fun getSoundOptions(path: String): Flow<List<SoundOption>> {
        return flow {
            val files = assetDataSource.getAssetFiles(path)
            val soundOptions = files.map { SoundOption(it) }
            emit(soundOptions)
        }
    }
}