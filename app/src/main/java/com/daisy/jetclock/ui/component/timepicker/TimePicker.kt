package com.daisy.jetclock.ui.component.timepicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daisy.jetclock.constants.TimeFormat
import com.daisy.jetclock.ui.component.drawable.RoundedLine
import com.daisy.jetclock.ui.theme.JetClockTheme

@Composable
fun TimePicker(
    timeFormat: TimeFormat = TimeFormat.Hour12Format,
    soundEnabled: Boolean = true,
) {
    val timeFormatter = TimeFormatter(timeFormat)
    val meridiemOptions = listOf("", "", "AM", "PM", "", "")

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        RoundedLine(
            colors = listOf(
                MaterialTheme.colors.primary,
                MaterialTheme.colors.secondaryVariant,
                MaterialTheme.colors.primaryVariant,
            ),
            modifier = Modifier.padding(top = 8.dp, end = 16.dp)
        )
        WheelPicker(
            items = timeFormatter.hours,
            initialIndex = 0,
            alignment = Alignment.Center,
            soundEnabled = soundEnabled,
        )
        Text(
            text = ":",
            modifier = Modifier.align(Alignment.CenterVertically),
            color = MaterialTheme.colors.primaryVariant,
            fontSize = 28.sp,
        )
        WheelPicker(
            items = timeFormatter.minutes,
            initialIndex = 0,
            alignment = Alignment.Center,
            soundEnabled = soundEnabled,
        )
        if (timeFormat == TimeFormat.Hour12Format) {
            WheelPicker(
                items = meridiemOptions,
                initialIndex = 1,
                alignment = Alignment.Center,
                isInfinite = false,
                soundEnabled = soundEnabled,
            )
        }
        RoundedLine(
            colors = listOf(
                MaterialTheme.colors.primaryVariant,
                MaterialTheme.colors.secondaryVariant,
                MaterialTheme.colors.primary,
            ),
            modifier = Modifier.padding(top = 8.dp, start = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimePickerPreview() {
    JetClockTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            TimePicker()
        }
    }
}