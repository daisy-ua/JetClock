package com.daisy.jetclock.presentation.ui.component.alarm


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.model.TimeFormat
import com.daisy.jetclock.presentation.utils.formatter.RepeatDaysFormatter
import com.daisy.jetclock.presentation.utils.formatter.TimeFormatter
import com.daisy.jetclock.presentation.utils.formatter.getLocalizedString
import com.daisy.jetclock.presentation.viewmodel.AlarmViewModel


@Composable
fun AlarmCard(
    item: Alarm,
    timeFormat: TimeFormat,
    viewModel: AlarmViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val repeatDaysString by rememberUpdatedState(
        RepeatDaysFormatter.formatRepeatDaysString(
            context,
            item.repeatDays
        )
    )

    val time by remember(item.time.hour, item.time.minute, timeFormat) {
        mutableStateOf(item.time.format(timeFormat))
    }

    val timeString by remember(time) {
        mutableStateOf(TimeFormatter.formatTime(context, time))
    }

    val changeCheckedState: (Alarm) -> Unit = remember {
        { alarm ->
            viewModel.changeCheckedState(alarm) { timeUntilAlarm ->
                TimeFormatter.formatTimeUntilAlarmGoesOff(context, timeUntilAlarm)
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .fillMaxWidth()
    ) {
        Column {
            Row {
                Text(
                    text = timeString,
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Medium
                )
                time.meridiem?.let { amPm ->
                    Text(
                        text = amPm.getLocalizedString(context),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.height(IntrinsicSize.Min)
            ) {
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.onBackground.copy(0.6f)
                )

                Text(
                    text = repeatDaysString,
                    style = MaterialTheme.typography.subtitle2,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colors.onBackground.copy(0.6f),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                )
            }
        }

        Switch(
            checked = item.isEnabled,
            onCheckedChange = { changeCheckedState(item) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AlarmCardPreview() {
//    AlarmCard({})
}