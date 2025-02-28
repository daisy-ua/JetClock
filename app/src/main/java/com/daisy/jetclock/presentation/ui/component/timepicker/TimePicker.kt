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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daisy.jetclock.domain.model.MeridiemOption
import com.daisy.jetclock.domain.model.TimeFormat
import com.daisy.jetclock.domain.model.TimeOfDay
import com.daisy.jetclock.presentation.ui.theme.JetClockTheme
import com.daisy.jetclock.presentation.utils.formatter.getLocalizedString
import com.daisy.jetclock.presentation.utils.helper.TimeDisplayHelper

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
    val context = LocalContext.current

    val timeDisplayHelper by remember {
        mutableStateOf(TimeDisplayHelper(timeFormat))
    }

    var hourValue by remember { mutableIntStateOf(0) }
    var minuteValue by remember { mutableIntStateOf(0) }
    var meridiemValue by remember { mutableStateOf<MeridiemOption?>(null) }

    var initialHourIndex by remember { mutableIntStateOf(0) }
    var initialMinuteIndex by remember { mutableIntStateOf(0) }
    var initialMeridiemIndex by remember { mutableIntStateOf(0) }

    fun handleHourChange(hourIndex: Int) {
        hourValue = timeDisplayHelper.getHourValue(hourIndex)
        onValueChange(TimeOfDay(hourValue, minuteValue, meridiemValue))
    }

    fun handleMinuteChange(minuteIndex: Int) {
        minuteValue = minuteIndex
        onValueChange(TimeOfDay(hourValue, minuteValue, meridiemValue))
    }

    fun handleMeridiemChange(meridiem: Int) {
        val newValue = timeDisplayHelper.getMeridiemOptions(meridiem)
        meridiemValue = newValue
        onValueChange(TimeOfDay(hourValue, minuteValue, meridiemValue))
    }

    LaunchedEffect(initialTimeValue) {
        hourValue = initialTimeValue.hour
        minuteValue = initialTimeValue.minute
        meridiemValue = initialTimeValue.meridiem

        initialHourIndex = timeDisplayHelper.getHourIndex(initialTimeValue.hour)
        initialMinuteIndex = timeDisplayHelper.getMinuteIndex(initialTimeValue.minute)
        initialMeridiemIndex = initialTimeValue.meridiem?.let {
            timeDisplayHelper.getIndexOfMeridiem(it)
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
            items = timeDisplayHelper.hours,
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
            items = timeDisplayHelper.minutes,
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
                items = timeDisplayHelper.meridiemOptions.map {
                    if (it is MeridiemOption) it.getLocalizedString(context)
                    else it as String
                },
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