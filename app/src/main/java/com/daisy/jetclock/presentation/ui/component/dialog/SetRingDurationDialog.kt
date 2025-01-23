package com.daisy.jetclock.presentation.ui.component.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daisy.jetclock.R
import com.daisy.jetclock.constants.AlarmOptionsData
import com.daisy.jetclock.presentation.ui.component.components.CancelOKButtonsRow
import com.daisy.jetclock.presentation.ui.component.components.TextRadioButtonRowItem
import com.daisy.jetclock.presentation.ui.theme.JetClockTheme

val ringDurationOptions by lazy {
    AlarmOptionsData.ringDurationOption
}

@Composable
fun SetRingDurationDialog(
    currentDuration: Int,
    onDismissRequest: () -> Unit,
    onSubmitRequest: (Int) -> Unit,
) {
    var durationSelected by rememberSaveable {
        mutableIntStateOf(currentDuration)
    }

    BottomFixedDialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.set_ring_duration_action),
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(ringDurationOptions) { _, option ->
                    TextRadioButtonRowItem(
                        name = pluralStringResource(
                            id = R.plurals.time_part_minute_duration,
                            count = option,
                            option
                        ),
                        isSelected = durationSelected == option,
                        onItemClick = { durationSelected = option })
                }
            }

            CancelOKButtonsRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 4.dp),
                onDismissRequest = onDismissRequest,
                onSubmitRequest = { onSubmitRequest(durationSelected) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SetRingDurationDialogPreview() {
    JetClockTheme(darkTheme = false) {
//        SetRingDurationDialog(onDismissRequest = {}, onSubmitRequest = {})
    }
}