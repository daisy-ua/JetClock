package com.daisy.jetclock.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daisy.jetclock.R
import com.daisy.jetclock.constants.AlarmOptionsData
import com.daisy.jetclock.constants.DefaultAlarmConfig.Companion.NEW_ALARM_ID
import com.daisy.jetclock.constants.MeridiemOption
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.model.RepeatDays
import com.daisy.jetclock.domain.model.RingDurationOption
import com.daisy.jetclock.domain.model.SnoozeOption
import com.daisy.jetclock.domain.model.SoundOption
import com.daisy.jetclock.domain.model.TimeOfDay
import com.daisy.jetclock.presentation.ui.component.button.DismissButton
import com.daisy.jetclock.presentation.ui.component.button.SwipeableButton
import com.daisy.jetclock.presentation.ui.theme.JetClockTheme
import com.daisy.jetclock.presentation.utils.formatter.DateFormatter
import com.daisy.jetclock.presentation.utils.formatter.TimeFormatter
import com.daisy.jetclock.presentation.utils.helper.TimeMillisUtils
import com.daisy.jetclock.presentation.viewmodel.OngoingAlarmViewModel


@Composable
fun OngoingAlarmScreen(
    viewModel: OngoingAlarmViewModel = hiltViewModel(),
) {
    val alarm by viewModel.alarm.collectAsStateWithLifecycle()

    alarm?.let {
        OngoingAlarmScreenContent(
            alarm = alarm!!,
            onSnoozeClicked = viewModel::snoozeAlarm,
            onDismissClicked = viewModel::dismissAlarm
        )
    }
}

@Composable
fun OngoingAlarmScreenContent(
    alarm: Alarm,
    onSnoozeClicked: () -> Unit,
    onDismissClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primaryVariant)
            .padding(start = 16.dp, end = 16.dp, bottom = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        AlarmDetails(alarm)

        AlarmActions(
            alarm = alarm,
            onSnoozeClicked = onSnoozeClicked,
            onDismissClicked = onDismissClicked
        )
    }
}

@Composable
private fun AlarmDetails(
    alarm: Alarm,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val formattedTime = remember {
        alarm.triggerTime?.let { timeInMillis ->
            val time = TimeMillisUtils.convertToTimeOfDay(timeInMillis)
            TimeFormatter.formatTime(context, time)
        } ?: "-/-"
    }
    val formattedDate = remember {
        alarm.triggerTime?.let { time ->
            DateFormatter.formatTriggerDate(time)
        } ?: DateFormatter.formatCurrentDate()
    }

    Column(
        modifier = modifier.padding(top = 72.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.wrapContentHeight(),
            text = formattedTime,
            style = MaterialTheme.typography.h1,
            color = MaterialTheme.colors.onBackground,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = formattedDate,
            style = MaterialTheme.typography.h6,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = alarm.label,
            style = MaterialTheme.typography.h6,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AlarmActions(
    alarm: Alarm,
    onSnoozeClicked: () -> Unit,
    onDismissClicked: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SwipeableButton(
            modifier = Modifier.fillMaxWidth(),
            action = onDismissClicked
        ) {
            DismissButton()
        }

        Button(
            modifier = Modifier.padding(top = 64.dp),
            onClick = onSnoozeClicked,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Black.copy(.7f)
            )
        ) {
            Text(
                text = pluralStringResource(
                    id = R.plurals.snooze_time_text,
                    count = alarm.snoozeOption.duration,
                    alarm.snoozeOption.duration
                ),
                color = Color.White
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun OngoingAlarmScreenPreview() {
    JetClockTheme(darkTheme = true) {
        OngoingAlarmScreenContent(
            alarm = Alarm(
                id = NEW_ALARM_ID,
                time = TimeOfDay(8, 47, MeridiemOption.PM),
                repeatDays = RepeatDays(listOf()),
                isEnabled = true,
                triggerTime = null,
                label = "Alarm",
                ringDurationOption = RingDurationOption(AlarmOptionsData.ringDurationOption[1]),
                snoozeOption = SnoozeOption(
                    duration = AlarmOptionsData.snoozeDuration[1],
                    number = AlarmOptionsData.snoozeNumber[1],
                ),
                snoozeCount = 0,
                soundOption = SoundOption.default
            ),
            {}, {}
        )
    }
}