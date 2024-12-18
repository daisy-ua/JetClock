package com.daisy.jetclock.utils.toast

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ToastStateHandler @Inject constructor(
    private val toastManager: ToastManager
) {
    private val _toastMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun setToastMessage(msg: String) {
        _toastMessage.value = msg
    }

    fun triggerToast() {
        toastManager.clearToast()
        toastMessage.value?.let {
            toastManager.showToast(it, ::clearToastMessage)
        }
    }
}