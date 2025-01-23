package com.daisy.jetclock.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daisy.jetclock.R
import com.daisy.jetclock.constants.AlarmOptionsData
import com.daisy.jetclock.constants.DefaultAlarmConfig
import com.daisy.jetclock.constants.DefaultAlarmConfig.Companion.NEW_ALARM_ID
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.model.MeridiemOption
import com.daisy.jetclock.domain.model.RepeatDays
import com.daisy.jetclock.domain.model.SnoozeOption
import com.daisy.jetclock.domain.model.SoundOption
import com.daisy.jetclock.domain.model.TimeFormat
import com.daisy.jetclock.domain.model.TimeOfDay
import com.daisy.jetclock.presentation.ui.component.alarm.AlarmList
import com.daisy.jetclock.presentation.ui.component.alarm.NextAlarmCard
import com.daisy.jetclock.presentation.ui.component.scaffold.JetClockFloatingActionButton
import com.daisy.jetclock.presentation.ui.component.scaffold.JetClockTopAppBar
import com.daisy.jetclock.presentation.ui.component.utils.ToastHandler
import com.daisy.jetclock.presentation.ui.theme.JetClockTheme
import com.daisy.jetclock.presentation.utils.formatter.TimeFormatter
import com.daisy.jetclock.presentation.utils.next.TimeUntilNextAlarm
import com.daisy.jetclock.presentation.viewmodel.AlarmViewModel
import com.daisy.jetclock.presentation.viewmodel.UIConfigurationViewModel

@Composable
fun AlarmScreen(
    onAlarmClick: (Long) -> Unit,
    viewModel: AlarmViewModel = hiltViewModel<AlarmViewModel>(),
    configViewModel: UIConfigurationViewModel = hiltViewModel(),
) {
    val timeFormat by configViewModel.timeFormat.collectAsStateWithLifecycle()
    val nextAlarm by viewModel.nextAlarm.collectAsStateWithLifecycle()
    val nextAlarmRingInTime by viewModel.nextAlarmTime.collectAsStateWithLifecycle()
    val alarmList by viewModel.alarms.collectAsStateWithLifecycle()

    val deleteAlarm: (Alarm) -> Unit = remember {
        { item ->
            viewModel.deleteAlarm(item)
        }
    }

    AlarmScreenContent(
        nextAlarm = nextAlarm,
        nextAlarmRingInTime = nextAlarmRingInTime,
        alarmList = alarmList,
        timeFormat = timeFormat,
        onNewAlarmClick = { onAlarmClick(DefaultAlarmConfig.NEW_ALARM_ID) },
        onExistingAlarmClick = onAlarmClick,
        onStartRefreshingNextAlarm = viewModel::startRefreshingNextAlarmTime,
        onStopRefreshingNextAlarm = viewModel::stopRefreshingNextAlarmTime,
        onAlarmDelete = deleteAlarm,
    )

    ToastHandler(viewModel.toastStateHandler)
}

@Composable
fun AlarmScreenContent(
    nextAlarm: Alarm?,
    nextAlarmRingInTime: TimeUntilNextAlarm?,
    alarmList: List<Alarm>,
    timeFormat: TimeFormat,
    onNewAlarmClick: () -> Unit,
    onExistingAlarmClick: (Long) -> Unit,
    onAlarmDelete: (Alarm) -> Unit,
    onStartRefreshingNextAlarm: () -> Unit,
    onStopRefreshingNextAlarm: () -> Unit,
) {
    val context = LocalContext.current

    Scaffold(
        topBar = { JetClockTopAppBar() },
        floatingActionButton = {
            JetClockFloatingActionButton(onNewAlarmClick)
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) {
        Column {
            AnimatedVisibility(
                visible = alarmList.isNotEmpty(),
                enter = EnterTransition.None,
                exit = slideOutVertically() + shrinkVertically() + fadeOut()
            ) {
                NextAlarmCard(
                    alarm = nextAlarm,
                    timeFormat = timeFormat,
                    ringInTime = nextAlarmRingInTime?.let { time ->
                        TimeFormatter.formatTimeUntilAlarmGoesOff(
                            context,
                            time
                        )
                    } ?: stringResource(id = R.string.no_alarm_scheduled_message),
                    onClick = onExistingAlarmClick,
                    onStartRefreshing = onStartRefreshingNextAlarm,
                    onStopRefreshing = onStopRefreshingNextAlarm
                )
            }

            if (alarmList.isEmpty()) {
                EmptyAlarmState(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 64.dp
                    )
                )
            }

            AlarmList(alarmList, timeFormat, onExistingAlarmClick, onAlarmDelete)
        }
    }
}

@Composable
fun EmptyAlarmState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.no_alarms_message),
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AlarmScreenPreview() {
    JetClockTheme(darkTheme = true) {
        val alarms = listOf(
            Alarm(
                id = NEW_ALARM_ID,
                time = TimeOfDay(1, 40, MeridiemOption.AM),
                repeatDays = RepeatDays(listOf()),
                isEnabled = true,
                triggerTime = 178177384687438,
                label = "Alarm",
                ringDuration = AlarmOptionsData.ringDurationOption[1],
                snoozeOption = SnoozeOption(
                    duration = AlarmOptionsData.snoozeDuration[1],
                    number = AlarmOptionsData.snoozeNumber[1],
                ),
                snoozeCount = 0,
                soundOption = SoundOption.default
            )
        )
        AlarmScreenContent(
            nextAlarm = alarms[0],
            nextAlarmRingInTime = TimeUntilNextAlarm(1, 5, 9),
            alarmList = alarms,
            timeFormat = TimeFormat.Hour12Format,
            onNewAlarmClick = { /*TODO*/ },
            onExistingAlarmClick = {},
            onAlarmDelete = {},
            onStartRefreshingNextAlarm = { /*TODO*/ },
            onStopRefreshingNextAlarm = {},
        )
    }
}