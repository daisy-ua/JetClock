package com.daisy.jetclock.data.repository.utils

import android.util.Log

private const val TAG = "DatastoreRepository"

suspend fun tryIt(action: suspend () -> Unit) {
    try {
        action()
    } catch (exception: Exception) {
        exception.localizedMessage?.let { Log.e(TAG, it) }
    }
}