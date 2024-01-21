package com.daisy.jetclock.ui.component.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daisy.jetclock.constants.AlarmOptionsData
import com.daisy.jetclock.domain.RingDurationOption
import com.daisy.jetclock.ui.component.components.CancelOKButtonsRow
import com.daisy.jetclock.ui.component.components.TextRadioButtonRowItem
import com.daisy.jetclock.ui.theme.JetClockTheme

val ringDurationOptions by lazy {
    AlarmOptionsData.ringDurationOption.map { duration ->
        val option = "$duration minutes"
        RingDurationOption(
            value = duration,
            displayString = if (duration == 1) option.dropLast(1) else option
        )
    }
}

@Composable
fun SetRingDurationDialog(
    onDismissRequest: () -> Unit,
    onSubmitRequest: () -> Unit,
    defaultDurationIndex: Int = 1,
) {
    val durationIndexSelected = rememberSaveable { mutableStateOf(defaultDurationIndex) }

    BottomFixedDialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Set ring duration",
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(ringDurationOptions) { index, option ->
                    TextRadioButtonRowItem(
                        name = option.displayString,
                        isSelected = durationIndexSelected.value == index,
                        onItemClick = { durationIndexSelected.value = index })
                }
            }

            CancelOKButtonsRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 4.dp),
                onDismissRequest = onDismissRequest,
                onSubmitRequest = onSubmitRequest,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SetRingDurationDialogPreview() {
    JetClockTheme(darkTheme = false) {
        SetRingDurationDialog(onDismissRequest = {}, onSubmitRequest = {})
    }
}