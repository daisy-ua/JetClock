package com.daisy.jetclock.data.source

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AssetDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun getAssetFiles(path: String): List<String> {
        return context.assets.list(path)?.toList() ?: emptyList()
    }

    fun getAfd(file: String): AssetFileDescriptor? {
        return try {
            context.assets.openFd(file)
        } catch (e: Exception) {
            Log.e("AssetDataSource", "Cannot open file: $file", e)
            null
        }
    }
}