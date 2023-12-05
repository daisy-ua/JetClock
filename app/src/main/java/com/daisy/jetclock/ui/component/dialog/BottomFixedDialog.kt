package com.daisy.jetclock.ui.component.dialog

import android.view.Gravity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider

@Composable
fun BottomFixedDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        val dialogWindowProvider = LocalView.current.parent as DialogWindowProvider
        dialogWindowProvider.window.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)

        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier
                .padding(bottom = 20.dp)
                .clip(MaterialTheme.shapes.medium)
                .fillMaxWidth()
        ) {
            content()
        }
    }
}