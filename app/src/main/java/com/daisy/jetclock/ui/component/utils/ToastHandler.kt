package com.daisy.jetclock.ui.component.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.daisy.jetclock.viewmodels.AlarmViewModel

@Composable
fun ToastHandler(viewModel: AlarmViewModel) {
    val toastMessage by viewModel.toastStateHandler.toastMessage.collectAsState()

    LaunchedEffect(toastMessage) {
        viewModel.toastStateHandler.triggerToast()
    }
}