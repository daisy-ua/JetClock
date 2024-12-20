package com.daisy.jetclock.presentation.ui.component.timepicker

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daisy.jetclock.constants.MeridiemOption
import com.daisy.jetclock.constants.TimeFormat
import com.daisy.jetclock.domain.model.TimeOfDay
import com.daisy.jetclock.presentation.ui.theme.JetClockTheme
import com.daisy.jetclock.utils.formatter.TimeFormatter

private val meridiemOptions = listOf("", "", MeridiemOption.AM.name, MeridiemOption.PM.name, "", "")

/**
 * Note: if a value is quickly selected and the save action is triggered immediately,
 * the updated value may not be emitted in time for the save operation
 */
@Composable
fun TimePicker(
    initialTimeValue: TimeOfDay,
    onValueChange: (TimeOfDay) -> Unit,
    modifier: Modifier = Modifier,
    timeFormat: TimeFormat = TimeFormat.Hour12Format,
    soundEnabled: Boolean = false,
    fontColor: Color = MaterialTheme.colors.onBackground,
) {
    val timeFormatter by remember {
        mutableStateOf(TimeFormatter(timeFormat))
    }

    var hourValue by remember { mutableIntStateOf(0) }
    var minuteValue by remember { mutableIntStateOf(0) }
    var meridiemValue by remember { mutableStateOf<MeridiemOption?>(null) }

    var initialHourIndex by remember { mutableIntStateOf(0) }
    var initialMinuteIndex by remember { mutableIntStateOf(0) }
    var initialMeridiemIndex by remember { mutableIntStateOf(0) }

    fun handleHourChange(hourIndex: Int) {
        hourValue = timeFormatter.hoursRange[hourIndex]
        onValueChange(TimeOfDay(hourValue, minuteValue, meridiemValue))
    }

    fun handleMinuteChange(minuteIndex: Int) {
        minuteValue = minuteIndex
        onValueChange(TimeOfDay(hourValue, minuteValue, meridiemValue))
    }

    fun handleMeridiemChange(meridiem: Int) {
        val newValue = MeridiemOption.valueOf(meridiemOptions[meridiem])
        meridiemValue = newValue
        onValueChange(TimeOfDay(hourValue, minuteValue, meridiemValue))
    }

    LaunchedEffect(initialTimeValue) {
        hourValue = initialTimeValue.hour
        minuteValue = initialTimeValue.minute
        meridiemValue = initialTimeValue.meridiem

        initialHourIndex = timeFormatter.hoursRange.indexOf(initialTimeValue.hour)
        initialMinuteIndex = timeFormatter.minutesRange.indexOf(initialTimeValue.minute)
        initialMeridiemIndex = initialTimeValue.meridiem?.let {
            meridiemOptions.indexOf(it.name) - 2
        } ?: 0
    }

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
            initialIndex = initialHourIndex,
            onValueChange = ::handleHourChange,
            alignment = Alignment.Center,
            soundEnabled = soundEnabled,
            fontColor = fontColor,
        )
        Text(
            text = ":",
            modifier = Modifier
                .height(35.dp)
                .align(Alignment.CenterVertically),
            color = fontColor,
            fontSize = 28.sp,
        )
        WheelPicker(
            items = timeFormatter.minutes,
            itemHeight = 35.dp,
            itemWidth = 55.dp,
            initialIndex = initialMinuteIndex,
            onValueChange = ::handleMinuteChange,
            alignment = Alignment.Center,
            soundEnabled = soundEnabled,
            fontColor = fontColor,
        )
        if (timeFormat == TimeFormat.Hour12Format) {
            WheelPicker(
                items = meridiemOptions,
                itemHeight = 35.dp,
                itemWidth = 55.dp,
                initialIndex = initialMeridiemIndex,
                onValueChange = ::handleMeridiemChange,
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
//            TimePicker()
        }
    }
}