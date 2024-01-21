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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.daisy.jetclock.constants.DayOfWeek
import com.daisy.jetclock.domain.Alarm
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
    var selectedRepeatDays by remember {
        mutableStateOf<List<DayOfWeek>>(emptyList())
    }

    var sound by remember {
        mutableStateOf("Default")
    }

    var showDialogType by remember {
        mutableStateOf(DialogType.NONE)
    }

    when (showDialogType) {
        DialogType.ALARM_LABEL -> {
            SetAlarmLabelDialog(
                onDismissRequest = { showDialogType = DialogType.NONE },
                onSubmitRequest = { showDialogType = DialogType.NONE }
            )
        }

        DialogType.RING_DURATION -> {
            SetRingDurationDialog(
                onDismissRequest = { showDialogType = DialogType.NONE },
                onSubmitRequest = { showDialogType = DialogType.NONE },
            )
        }

        DialogType.SNOOZE_DURATION -> {
            SetSnoozeDurationDialog(
                onDismissRequest = { showDialogType = DialogType.NONE },
                onSubmitRequest = { showDialogType = DialogType.NONE }
            )
        }

        DialogType.NONE -> {}
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    val saveAlarm: () -> Unit = {
        val alarm = Alarm(
            id = alarmId,
            hour = 1,
            minute = 10,
            meridiem = null,
            repeatDays = emptyList(),
            isEnabled = true,
            label = "label",
            ringDuration = 10,
            snoozeDuration = 1,
            snoozeNumber = 1,
            sound = "sound"
        )

        if (!isSaving) {
            isSaving = true
            viewModel.insertAlarm(alarm)
        }

        onUpClick()
    }

    Scaffold(
        topBar = {
            JetClockFuncTopAppBar(
                title = "Set alarm",
                onClose = onUpClick,
                onApply = saveAlarm
            )
        },
        floatingActionButton = { TextFloatingActionButton(Modifier.padding(bottom = 8.dp)) },
        floatingActionButtonPosition = FabPosition.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TimePicker(
                modifier = Modifier.padding(top = 4.dp, bottom = 26.dp, start = 16.dp, end = 16.dp),
                soundEnabled = false,
            )

            RepeatSetting(darkThemeEnabled)

            LazyColumn {
                item {
                    SettingRow("Sound", "Default") { onSelectSoundClicked() }
                }

                item {
                    SettingRow("Label", "Alarm") {
                        showDialogType = DialogType.ALARM_LABEL
                    }
                }

                item {
                    SettingRow("Ring duration", "5 minutes") {
                        showDialogType = DialogType.RING_DURATION
                    }
                }

                item {
                    SettingRow("Snooze duration", "10 minutes, 3x") {
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
fun RepeatSetting(darkThemeEnabled: Boolean) {
    Text(
        text = "Repeat",
        modifier = Modifier.padding(start = 20.dp, top = 8.dp),
        style = MaterialTheme.typography.body1,
        color = MaterialTheme.colors.onBackground,
        fontWeight = FontWeight.Medium
    )

    RepeatDays(
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