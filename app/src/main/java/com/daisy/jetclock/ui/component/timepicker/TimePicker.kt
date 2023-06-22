package com.daisy.jetclock.ui.component.timepicker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daisy.jetclock.constants.TimeFormat
import com.daisy.jetclock.ui.theme.JetClockTheme

@Composable
fun TimePicker(
    timeFormat: TimeFormat = TimeFormat.Hour12Format,
) {
    val timeFormatter = TimeFormatter.apply { setTimeFormat(timeFormat) }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Spacer(
            modifier = Modifier
                .padding(top = 8.dp, end = 16.dp)
                .height(2.dp)
                .width(50.dp)
                .background(MaterialTheme.colors.onBackground)
                .align(Alignment.CenterVertically)
        )
        WheelPicker(items = timeFormatter.hours, firstIndex = 0, alignment = Alignment.CenterStart)
        Text(":", modifier = Modifier.align(Alignment.CenterVertically), fontSize = 28.sp)
        WheelPicker(items = timeFormatter.minutes, firstIndex = 0, alignment = Alignment.CenterEnd)
        Spacer(
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp)
                .height(2.dp)
                .width(50.dp)
                .background(MaterialTheme.colors.onBackground)
                .align(Alignment.CenterVertically)
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