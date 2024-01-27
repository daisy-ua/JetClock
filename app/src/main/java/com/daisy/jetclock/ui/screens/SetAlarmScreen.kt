package com.daisy.jetclock.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daisy.jetclock.domain.RepeatDays
import com.daisy.jetclock.ui.component.components.ListRowComponent
import com.daisy.jetclock.ui.component.dialog.SetAlarmLabelDialog
import com.daisy.jetclock.ui.component.dialog.SetRingDurationDialog
import com.daisy.jetclock.ui.component.dialog.SetSnoozeDurationDialog
import com.daisy.jetclock.ui.component.repeatdays.RepeatDays
import com.daisy.jetclock.ui.component.scaffold.JetClockFuncTopAppBar
import com.daisy.jetclock.ui.component.scaffold.TextFloatingActionButton
import com.daisy.jetclock.ui.component.timepicker.TimePicker
import com.daisy.jetclock.ui.theme.JetClockTheme
import com.daisy.jetclock.viewmodels.NewAlarmViewModel

private enum class DialogType {
    NONE,
    ALARM_LABEL,
    RING_DURATION,
    SNOOZE_DURATION
}

@Composable
fun SetAlarmScreen(
    alarmId: Long,
    onUpClick: () -> Unit,
    onSelectSoundClicked: () -> Unit,
    viewModel: NewAlarmViewModel = hiltViewModel<NewAlarmViewModel>(),
    darkThemeEnabled: Boolean = isSystemInDarkTheme(),
) {
    LaunchedEffect(key1 = alarmId) {
        viewModel.getAlarmById(alarmId)
    }

    val label by viewModel.label.collectAsStateWithLifecycle()
    val ringDuration by viewModel.ringDuration.collectAsStateWithLifecycle()
    val snoozeDuration by viewModel.snoozeDuration.collectAsStateWithLifecycle()
    val repeatDays by viewModel.repeatDays.collectAsStateWithLifecycle()
    val time by viewModel.time.collectAsStateWithLifecycle()

    var showDialogType by remember {
        mutableStateOf(DialogType.NONE)
    }

    when (showDialogType) {
        DialogType.ALARM_LABEL -> {
            SetAlarmLabelDialog(
                value = label,
                onDismissRequest = { showDialogType = DialogType.NONE },
                onSubmitRequest = { labelValue ->
                    viewModel.updateLabel(labelValue)
                    showDialogType = DialogType.NONE
                }
            )
        }

        DialogType.RING_DURATION -> {
            SetRingDurationDialog(
                currentDurationOption = ringDuration,
                onDismissRequest = { showDialogType = DialogType.NONE },
                onSubmitRequest = { ringDurationOption ->
                    viewModel.updateRingDuration(ringDurationOption)
                    showDialogType = DialogType.NONE
                },
            )
        }

        DialogType.SNOOZE_DURATION -> {
            SetSnoozeDurationDialog(
                currentSnoozeOption = snoozeDuration,
                onDismissRequest = { showDialogType = DialogType.NONE },
                onSubmitRequest = { snoozeOption ->
                    viewModel.updateSnoozeDuration(snoozeOption)
                    showDialogType = DialogType.NONE
                }
            )
        }

        DialogType.NONE -> {}
    }

    Scaffold(
        topBar = {
            JetClockFuncTopAppBar(
                title = "Set alarm",
                onClose = onUpClick,
                onApply = { viewModel.saveAlarm(onUpClick) }
            )
        },
        floatingActionButton = {
            TextFloatingActionButton(
                onItemClick = {},
                Modifier.padding(bottom = 8.dp)
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TimePicker(
                initialTimeValue = time,
                onValueChange = { hour, minute, meridiem ->
                    viewModel.updateTime(
                        hour,
                        minute,
                        meridiem
                    )
                },
                modifier = Modifier.padding(top = 4.dp, bottom = 26.dp, start = 16.dp, end = 16.dp),
                soundEnabled = true,
            )

            RepeatSetting(darkThemeEnabled, repeatDays) { updRepeatDays ->
                viewModel.updateRepeatDays(updRepeatDays)
            }

            LazyColumn {
                item {
                    SettingRow("Sound", "Sound") {
                        onSelectSoundClicked()
                    }
                }

                item {
                    SettingRow("Label", label) {
                        showDialogType = DialogType.ALARM_LABEL
                    }
                }

                item {
                    SettingRow("Ring duration", ringDuration.displayString) {
                        showDialogType = DialogType.RING_DURATION
                    }
                }

                item {
                    SettingRow("Snooze duration", snoozeDuration.displayString) {
                        showDialogType = DialogType.SNOOZE_DURATION
                    }
                }
            }
        }
    }
}

@Composable
fun SettingRow(option: String, value: String, onItemClick: () -> Unit) {
    ListRowComponent(
        onItemClick = { onItemClick.invoke() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = option,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = value,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onBackground.copy(0.6f),
        )
    }
}

@Composable
fun RepeatSetting(darkThemeEnabled: Boolean, value: RepeatDays, onItemClick: (RepeatDays) -> Unit) {
    Text(
        text = "Repeat",
        modifier = Modifier.padding(start = 20.dp, top = 8.dp),
        style = MaterialTheme.typography.body1,
        color = MaterialTheme.colors.onBackground,
        fontWeight = FontWeight.Medium
    )

    RepeatDays(
        value = value,
        onItemClicked = onItemClick,
        modifier = Modifier
            .padding(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
        darkThemeEnabled = darkThemeEnabled
    )
}

@Preview(showBackground = true)
@Composable
fun SetAlarmScreenPreview() {
    JetClockTheme(darkTheme = true) {
//        SetAlarmScreen(0, {}, {})
    }
}