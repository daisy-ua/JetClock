package com.daisy.jetclock.ui.component.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daisy.jetclock.constants.AlarmOptionsData
import com.daisy.jetclock.domain.SnoozeOption
import com.daisy.jetclock.ui.component.components.CancelOKButtonsRow
import com.daisy.jetclock.ui.component.components.LabeledSlider
import com.daisy.jetclock.ui.theme.JetClockTheme

@Composable
fun SetSnoozeDurationDialog(
    currentSnoozeOption: SnoozeOption,
    onDismissRequest: () -> Unit,
    onSubmitRequest: (SnoozeOption) -> Unit,
) {
    var durationPosition by rememberSaveable { mutableFloatStateOf(currentSnoozeOption.duration.toFloat()) }

    var snoozeNumberIndex by remember {
        mutableFloatStateOf(
            AlarmOptionsData.snoozeNumber.indexOf(
                currentSnoozeOption.number
            ).toFloat()
        )
    }

    BottomFixedDialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Snooze duration",
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 12.dp)
            )

            val subMenuColor = MaterialTheme.colors.onBackground.copy(.6f)

            Text(
                text = "Snooze duration (min)",
                style = MaterialTheme.typography.body1,
                color = subMenuColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 10.dp)
            )

            LabeledSlider(
                value = durationPosition,
                onValueChange = { durationPosition = it },
                steps = 4,
                valueRange = 5f..30f,
                trackLabel = AlarmOptionsData.snoozeDuration,
                modifier = Modifier.padding(horizontal = 8.dp),
                labelColor = subMenuColor,
            )

            Text(
                text = "Number of snoozes",
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Medium,
                color = subMenuColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 30.dp, end = 16.dp, bottom = 10.dp)
            )

            LabeledSlider(
                value = snoozeNumberIndex,
                onValueChange = { snoozeNumberIndex = it },
                steps = 2,
                valueRange = 0f..3f,
                trackLabel = AlarmOptionsData.snoozeNumber,
                modifier = Modifier.padding(horizontal = 8.dp),
                labelColor = subMenuColor,
            )

            CancelOKButtonsRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 4.dp),
                onDismissRequest = onDismissRequest,
                onSubmitRequest = {
                    onSubmitRequest(
                        SnoozeOption(
                            durationPosition.toInt(),
                            AlarmOptionsData.snoozeNumber[snoozeNumberIndex.toInt()]
                        )
                    )
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SetSnoozeDurationDialogPreview() {
    JetClockTheme(darkTheme = false) {
//        SetSnoozeDurationDialog(onDismissRequest = {}, onSubmitRequest = {})
    }
}