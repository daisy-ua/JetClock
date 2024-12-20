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
import com.daisy.jetclock.constants.NewAlarmDefaults
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.presentation.ui.component.alarm.AlarmList
import com.daisy.jetclock.presentation.ui.component.alarm.NextAlarmCard
import com.daisy.jetclock.presentation.ui.component.scaffold.JetClockFloatingActionButton
import com.daisy.jetclock.presentation.ui.component.scaffold.JetClockTopAppBar
import com.daisy.jetclock.presentation.ui.component.utils.ToastHandler
import com.daisy.jetclock.presentation.ui.theme.JetClockTheme
import com.daisy.jetclock.utils.formatter.AlarmFormatter
import com.daisy.jetclock.presentation.viewmodel.AlarmViewModel

@Composable
fun AlarmScreen(
    onAlarmClick: (Long) -> Unit,
    viewModel: AlarmViewModel = hiltViewModel<AlarmViewModel>(),
) {
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
        onNewAlarmClick = { onAlarmClick(NewAlarmDefaults.NEW_ALARM_ID) },
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
    nextAlarmRingInTime: String?,
    alarmList: List<Alarm>,
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
                    alarmTime = nextAlarm?.let {
                        AlarmFormatter.getTimeString(
                            context,
                            it.hour,
                            it.minute
                        )
                    }
                        ?: "",
                    ringInTime = nextAlarmRingInTime
                        ?: stringResource(id = R.string.no_alarm_scheduled_message),
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

            AlarmList(alarmList, onExistingAlarmClick, onAlarmDelete)
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
        AlarmScreenContent(
            nextAlarm = null,
            nextAlarmRingInTime = "",
            alarmList = emptyList(),
            onNewAlarmClick = { /*TODO*/ },
            onExistingAlarmClick = {},
            onAlarmDelete = {},
            onStartRefreshingNextAlarm = { /*TODO*/ },
            onStopRefreshingNextAlarm = {},
        )
    }
}