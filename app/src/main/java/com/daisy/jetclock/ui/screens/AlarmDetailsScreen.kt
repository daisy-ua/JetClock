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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daisy.jetclock.R
import com.daisy.jetclock.constants.MeridiemOption
import com.daisy.jetclock.constants.NewAlarmDefaults
import com.daisy.jetclock.domain.Alarm
import com.daisy.jetclock.domain.RepeatDays
import com.daisy.jetclock.domain.RingDurationOption
import com.daisy.jetclock.domain.SnoozeOption
import com.daisy.jetclock.domain.SoundOption
import com.daisy.jetclock.domain.TimeOfDay
import com.daisy.jetclock.ui.component.components.ListRowComponent
import com.daisy.jetclock.ui.component.dialog.SetAlarmLabelDialog
import com.daisy.jetclock.ui.component.dialog.SetRingDurationDialog
import com.daisy.jetclock.ui.component.dialog.SetSnoozeDurationDialog
import com.daisy.jetclock.ui.component.repeatdays.RepeatDays
import com.daisy.jetclock.ui.component.scaffold.JetClockFuncTopAppBar
import com.daisy.jetclock.ui.component.scaffold.TextFloatingActionButton
import com.daisy.jetclock.ui.component.timepicker.TimePicker
import com.daisy.jetclock.ui.component.utils.ToastHandler
import com.daisy.jetclock.viewmodels.AlarmDetailsViewModel
import com.daisy.jetclock.viewmodels.SelectedSoundViewModel

enum class DialogType {
    NONE,
    ALARM_LABEL,
    RING_DURATION,
    SNOOZE_DURATION
}

@Composable
fun AlarmDetailsScreen(
    alarmId: Long,
    onUpClick: () -> Unit,
    onSelectSoundClicked: () -> Unit,
    viewModel: AlarmDetailsViewModel = hiltViewModel<AlarmDetailsViewModel>(),
    soundViewModel: SelectedSoundViewModel = hiltViewModel<SelectedSoundViewModel>(),
    darkThemeEnabled: Boolean = isSystemInDarkTheme(),
) {
    LaunchedEffect(alarmId) {
        viewModel.updateScreenData(alarmId)

        viewModel.setUpdateSoundViewModelCallback { soundFile ->
            soundViewModel.updateSelectedSound(soundFile)
        }
    }

    val alarm by viewModel.alarm.collectAsStateWithLifecycle()
    val sound by soundViewModel.selectedSound.collectAsStateWithLifecycle()

    LaunchedEffect(sound) {
        if (sound.soundFile != alarm.sound) {
            viewModel.updateSoundFile(sound.soundFile)
        }
    }

    var showDialogType by remember {
        mutableStateOf(DialogType.NONE)
    }

    HandleDialogs(
        label = alarm.label,
        ringDuration = RingDurationOption(alarm.ringDuration),
        snoozeDuration = with(alarm) { SnoozeOption(snoozeDuration, snoozeNumber) },
        showDialogType = showDialogType,
        onDialogDismiss = { showDialogType = DialogType.NONE },
        onSubmit = { updatedValue, dialogType ->
            handleDialogSubmit(dialogType, updatedValue, viewModel)
            showDialogType = DialogType.NONE
        })

    val onUpdateSelectedTime: (Int, Int, MeridiemOption?) -> Unit = remember {
        { hour, minute, meridiem ->
            viewModel.updateTime(
                hour,
                minute,
                meridiem
            )
        }
    }

    val onUpdateRepeatDays: (RepeatDays) -> Unit = remember {
        { updRepeatDays ->
            viewModel.updateRepeatDays(updRepeatDays)
        }
    }

    val onSaveAlarm: () -> Unit = remember {
        { viewModel.saveAlarm(onUpClick) }
    }

    val onDeleteAlarm: () -> Unit = remember {
        { viewModel.deleteAlarm(onUpClick) }
    }

    val onShowDialogTypeChanged: (DialogType) -> Unit = remember {
        { type ->
            showDialogType = type
        }
    }

    SetAlarmScreenContent(
        alarm = alarm,
        onSaveAlarm = onSaveAlarm,
        onDeleteAlarm = onDeleteAlarm,
        onUpdateSelectedTime = onUpdateSelectedTime,
        onUpdateRepeatDays = onUpdateRepeatDays,
        onSelectSoundClicked = onSelectSoundClicked,
        onUpClick = onUpClick,
        onShowDialogTypeChanged = onShowDialogTypeChanged,
        darkThemeEnabled = darkThemeEnabled
    )

    ToastHandler(viewModel.toastStateHandler)
}

@Composable
fun HandleDialogs(
    label: String,
    ringDuration: RingDurationOption,
    snoozeDuration: SnoozeOption,
    showDialogType: DialogType,
    onDialogDismiss: () -> Unit,
    onSubmit: (Any, DialogType) -> Unit,
) {
    when (showDialogType) {
        DialogType.ALARM_LABEL -> {
            SetAlarmLabelDialog(
                value = label,
                onDismissRequest = onDialogDismiss,
                onSubmitRequest = { labelValue -> onSubmit(labelValue, DialogType.ALARM_LABEL) }
            )
        }

        DialogType.RING_DURATION -> {
            SetRingDurationDialog(
                currentDurationOption = ringDuration,
                onDismissRequest = onDialogDismiss,
                onSubmitRequest = { ringDurationOption ->
                    onSubmit(
                        ringDurationOption,
                        DialogType.RING_DURATION
                    )
                }
            )
        }

        DialogType.SNOOZE_DURATION -> {
            SetSnoozeDurationDialog(
                currentSnoozeOption = snoozeDuration,
                onDismissRequest = onDialogDismiss,
                onSubmitRequest = { snoozeOption ->
                    onSubmit(
                        snoozeOption,
                        DialogType.SNOOZE_DURATION
                    )
                }
            )
        }

        DialogType.NONE -> {}
    }
}

fun handleDialogSubmit(dialogType: DialogType, updatedValue: Any, viewModel: AlarmDetailsViewModel) {
    when (dialogType) {
        DialogType.ALARM_LABEL -> viewModel.updateLabel(updatedValue as String)
        DialogType.RING_DURATION -> viewModel.updateRingDuration(updatedValue as RingDurationOption)
        DialogType.SNOOZE_DURATION -> viewModel.updateSnoozeDuration(updatedValue as SnoozeOption)
        DialogType.NONE -> {}
    }
}

@Composable
fun SetAlarmScreenContent(
    alarm: Alarm,
    onSaveAlarm: () -> Unit,
    onDeleteAlarm: () -> Unit,
    onUpdateSelectedTime: (Int, Int, MeridiemOption?) -> Unit,
    onUpdateRepeatDays: (RepeatDays) -> Unit,
    onSelectSoundClicked: () -> Unit,
    onUpClick: () -> Unit,
    onShowDialogTypeChanged: (DialogType) -> Unit,
    darkThemeEnabled: Boolean,
) {
    Scaffold(
        topBar = {
            JetClockFuncTopAppBar(
                title = stringResource(
                    id = if (alarm.id == NewAlarmDefaults.NEW_ALARM_ID)
                        R.string.set_alarm
                    else R.string.edit_alarm
                ),
                onClose = onUpClick,
                onApply = onSaveAlarm
            )
        },
        floatingActionButton = {
            if (alarm.id != NewAlarmDefaults.NEW_ALARM_ID) {
                TextFloatingActionButton(
                    onItemClick = onDeleteAlarm,
                    Modifier.padding(bottom = 8.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            TimePicker(
                initialTimeValue = with(alarm) { TimeOfDay(hour, minute, meridiem) },
                onValueChange = onUpdateSelectedTime,
                modifier = Modifier.padding(top = 4.dp, bottom = 26.dp, start = 16.dp, end = 16.dp),
                soundEnabled = true,
            )

            RepeatSetting(darkThemeEnabled, RepeatDays(alarm.repeatDays), onUpdateRepeatDays)

            LazyColumn {
                item {
                    SettingRow(
                        stringResource(id = R.string.sound),
                        SoundOption(alarm.sound).displayName,
                        onSelectSoundClicked
                    )
                }

                item {
                    SettingRow(stringResource(id = R.string.label), alarm.label) {
                        onShowDialogTypeChanged(DialogType.ALARM_LABEL)
                    }
                }

                item {
                    SettingRow(
                        stringResource(id = R.string.ring_duration),
                        RingDurationOption(alarm.ringDuration).displayString
                    ) {
                        onShowDialogTypeChanged(DialogType.RING_DURATION)
                    }
                }

                item {
                    SettingRow(
                        stringResource(id = R.string.snooze_duration),
                        SnoozeOption(alarm.snoozeDuration, alarm.snoozeNumber).displayString
                    ) {
                        onShowDialogTypeChanged(DialogType.SNOOZE_DURATION)
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
        text = stringResource(id = R.string.repeat),
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