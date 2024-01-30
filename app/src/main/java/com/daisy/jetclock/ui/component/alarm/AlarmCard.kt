package com.daisy.jetclock.ui.component.alarm


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.daisy.jetclock.domain.Alarm
import com.daisy.jetclock.viewmodels.AlarmViewModel


@Composable
fun AlarmCard(
    item: Alarm,
    onAlarmClick: (Long) -> Unit,
    viewModel: AlarmViewModel = hiltViewModel(),
) {
    val checkedState = remember {
        mutableStateOf(item.isEnabled)
    }

    val repeatDaysString by remember {
        val value = viewModel.getRepeatDaysString(item.repeatDays)
        mutableStateOf(value)
    }

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier
            .clickable {
                onAlarmClick(item.id)
            }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Column {
                Row {
                    Text(
                        text = viewModel.getTimeString(item.hour, item.minute),
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Medium
                    )
                    item.meridiem?.let { meridiem ->
                        Text(
                            text = meridiem.name,
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
                checked = checkedState.value,
                onCheckedChange = {
                    checkedState.value = it
                    viewModel.changeCheckedState(item, it)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlarmCardPreview() {
//    AlarmCard({})
}