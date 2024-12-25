package com.daisy.jetclock.utils

import android.content.res.AssetFileDescriptor
import com.daisy.jetclock.data.source.AssetDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetUtils @Inject constructor(
    private val assetDataSource: AssetDataSource,
) {
    fun getAfd(path: String, soundFile: String): AssetFileDescriptor? {
        val filePath = "$path/$soundFile"
        return assetDataSource.getAfd(filePath)
    }
}