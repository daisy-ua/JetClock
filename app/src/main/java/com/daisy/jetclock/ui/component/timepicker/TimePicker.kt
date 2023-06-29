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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daisy.jetclock.constants.TimeFormat
import com.daisy.jetclock.ui.theme.JetClockTheme

@Composable
fun TimePicker(
    modifier: Modifier = Modifier,
    timeFormat: TimeFormat = TimeFormat.Hour12Format,
    soundEnabled: Boolean = false,
    fontColor: Color = MaterialTheme.colors.onBackground,
) {
    val timeFormatter = TimeFormatter(timeFormat)
    val meridiemOptions = listOf("", "", "AM", "PM", "", "")

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
    ) {
        Spacer(
            modifier = Modifier
                .padding(top = 8.dp, end = 16.dp)
                .clip(MaterialTheme.shapes.small)
                .width(50.dp)
                .height(2.dp)
                .background(fontColor)
        )
        WheelPicker(
            items = timeFormatter.hours,
            itemHeight = 35.dp,
            itemWidth = 55.dp,
            initialIndex = 0,
            alignment = Alignment.Center,
            soundEnabled = soundEnabled,
            fontColor = fontColor,
        )
        Text(
            text = ":",
            modifier = Modifier
                .height(35.dp)
                .align(Alignment.CenterVertically)
                .wrapContentHeight(),
            color = fontColor,
            fontSize = 28.sp,
        )
        WheelPicker(
            items = timeFormatter.minutes,
            itemHeight = 35.dp,
            itemWidth = 55.dp,

            initialIndex = 0,
            alignment = Alignment.Center,
            soundEnabled = soundEnabled,
            fontColor = fontColor,
        )
        if (timeFormat == TimeFormat.Hour12Format) {
            WheelPicker(
                items = meridiemOptions,
                itemHeight = 35.dp,
                itemWidth = 55.dp,

                initialIndex = 1,
                alignment = Alignment.Center,
                isInfinite = false,
                soundEnabled = soundEnabled,
                fontColor = fontColor,
            )
        }
        Spacer(
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp)
                .clip(MaterialTheme.shapes.small)
                .width(50.dp)
                .height(2.dp)
                .background(fontColor)
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