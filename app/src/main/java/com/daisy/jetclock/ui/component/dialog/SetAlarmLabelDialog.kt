package com.daisy.jetclock.ui.component.dialog

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daisy.jetclock.ui.component.components.CancelOKButtonsRow
import com.daisy.jetclock.ui.theme.JetClockTheme
import kotlinx.coroutines.android.awaitFrame

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SetAlarmLabelDialog(
    value: String,
    onDismissRequest: () -> Unit,
    onSubmitRequest: (String) -> Unit,
) {
    val windowInfo = LocalWindowInfo.current
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(focusRequester) {
        snapshotFlow { windowInfo.isWindowFocused }.collect { isWindowFocused ->
            awaitFrame()
            if (isWindowFocused) {
                focusRequester.requestFocus()
                keyboard?.show()
            }
        }
    }

    var textFieldValueState by remember {
        mutableStateOf(
            TextFieldValue(
                text = value, selection = TextRange(0, value.length)
            )
        )
    }

    BottomFixedDialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = "Set alarm label",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = textFieldValueState,
                onValueChange = { textFieldValueState = it },
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colors.primary, MaterialTheme.colors.secondary
                            ),
                        ),
                        shape = MaterialTheme.shapes.medium
                    )
            )

            CancelOKButtonsRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 4.dp),
                onDismissRequest = onDismissRequest,
                onSubmitRequest = { onSubmitRequest(textFieldValueState.text) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SetAlarmLabelDialogPreview() {
    JetClockTheme(darkTheme = false) {
//        SetAlarmLabelDialog(onDismissRequest = {}, onSubmitRequest = {})
    }
}