package com.daisy.jetclock.ui.component.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daisy.jetclock.utils.toast.ToastStateHandler

@Composable
fun ToastHandler(toastStateHandler: ToastStateHandler) {
    val toastMessage by toastStateHandler.toastMessage.collectAsStateWithLifecycle()

    LaunchedEffect(toastMessage) {
        toastStateHandler.triggerToast()
    }
}