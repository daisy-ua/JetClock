package com.daisy.jetclock.utils

import android.content.Context
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class ToastManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private var currentToast: Toast? = null
    private var clearToastJob: Job? = null

    fun showToast(message: String, callback: () -> Unit, runAfterMillis: Long = 2000L) {
        showToast(message)
        runAfterDelay(runAfterMillis, callback)
    }

    fun showToast(message: String) {
        currentToast?.cancel()
        currentToast = Toast.makeText(context, message, Toast.LENGTH_LONG).apply {
            show()
        }
    }

    fun clearToast() {
        currentToast?.cancel()
    }

    private fun runAfterDelay(timeMillis: Long, callback: () -> Unit) {
        clearToastJob?.cancel()
        clearToastJob = CoroutineScope(Dispatchers.Main).launch {
            delay(timeMillis)
            callback.invoke()
        }
    }
}
