package com.daisy.jetclock.presentation.ui.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daisy.jetclock.R
import com.daisy.jetclock.constants.DefaultAlarmConfig
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.model.RepeatDays
import com.daisy.jetclock.domain.model.RingDurationOption
import com.daisy.jetclock.domain.model.SnoozeOption
import com.daisy.jetclock.domain.model.TimeFormat
import com.daisy.jetclock.domain.model.TimeOfDay
import com.daisy.jetclock.presentation.ui.component.components.ListRowComponent
import com.daisy.jetclock.presentation.ui.component.dialog.SetAlarmLabelDialog
import com.daisy.jetclock.presentation.ui.component.dialog.SetRingDurationDialog
import com.daisy.jetclock.presentation.ui.component.dialog.SetSnoozeDurationDialog
import com.daisy.jetclock.presentation.ui.component.repeatdays.RepeatDays
import com.daisy.jetclock.presentation.ui.component.scaffold.JetClockFuncTopAppBar
import com.daisy.jetclock.presentation.ui.component.scaffold.TextFloatingActionButton
import com.daisy.jetclock.presentation.ui.component.timepicker.TimePicker
import com.daisy.jetclock.presentation.ui.component.utils.ToastHandler
import com.daisy.jetclock.presentation.utils.formatter.SoundOptionFormatter
import com.daisy.jetclock.presentation.utils.formatter.TimeFormatter
import com.daisy.jetclock.presentation.viewmodel.AlarmDetailsViewModel

enum class DialogType {
    NONE,
    ALARM_LABEL,
    RING_DURATION,
    SNOOZE_DURATION
}

@Composable
fun AlarmDetailsScreen(
    onUpClick: () -> Unit,
    onSelectSoundClicked: (String) -> Unit,
    viewModel: AlarmDetailsViewModel = hiltViewModel<AlarmDetailsViewModel>(),
    darkThemeEnabled: Boolean = isSystemInDarkTheme(),
) {
    val alarm by viewModel.alarm.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showDialogType by remember {
        mutableStateOf(DialogType.NONE)
    }

    HandleDialogs(
        label = alarm.label,
        ringDuration = alarm.ringDurationOption,
        snoozeDuration = alarm.snoozeOption,
        showDialogType = showDialogType,
        onDialogDismiss = { showDialogType = DialogType.NONE },
        onSubmit = { updatedValue, dialogType ->
            handleDialogSubmit(dialogType, updatedValue, viewModel)
            showDialogType = DialogType.NONE
        })

    val onUpdateSelectedTime: (TimeOfDay) -> Unit = remember {
        { time ->
            viewModel.updateTime(time)
        }
    }

    val onUpdateRepeatDays: (RepeatDays) -> Unit = remember {
        { updRepeatDays ->
            viewModel.updateRepeatDays(updRepeatDays)
        }
    }

    val onSaveAlarm: () -> Unit = remember {
        {
            viewModel.saveAlarm(onUpClick) { timeUntilAlarm ->
                TimeFormatter.formatTimeUntilAlarmGoesOff(context, timeUntilAlarm)
            }
        }
    }

    val onDeleteAlarm: () -> Unit = remember {
        { viewModel.deleteAlarm(onUpClick) }
    }

    val onShowDialogTypeChanged: (DialogType) -> Unit = remember {
        { type ->
            showDialogType = type
        }
    }


    AlarmDetailsScreenContent(
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

fun handleDialogSubmit(
    dialogType: DialogType,
    updatedValue: Any,
    viewModel: AlarmDetailsViewModel,
) {
    when (dialogType) {
        DialogType.ALARM_LABEL -> viewModel.updateLabel(updatedValue as String)
        DialogType.RING_DURATION -> viewModel.updateRingDuration(updatedValue as RingDurationOption)
        DialogType.SNOOZE_DURATION -> viewModel.updateSnoozeDuration(updatedValue as SnoozeOption)
        DialogType.NONE -> {}
    }
}

@Composable
fun AlarmDetailsScreenContent(
    alarm: Alarm,
    onSaveAlarm: () -> Unit,
    onDeleteAlarm: () -> Unit,
    onUpdateSelectedTime: (TimeOfDay) -> Unit,
    onUpdateRepeatDays: (RepeatDays) -> Unit,
    onSelectSoundClicked: (String) -> Unit,
    onUpClick: () -> Unit,
    onShowDialogTypeChanged: (DialogType) -> Unit,
    darkThemeEnabled: Boolean,
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            JetClockFuncTopAppBar(
                title = stringResource(
                    id = if (isNewAlarm(alarm.id))
                        R.string.set_alarm
                    else R.string.edit_alarm
                ),
                onClose = onUpClick,
                onApply = onSaveAlarm
            )
        },
        floatingActionButton = {
            if (!isNewAlarm(alarm.id)) {
                TextFloatingActionButton(
                    actionText = stringResource(id = R.string.delete_action),
                    onItemClick = onDeleteAlarm,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
//            TODO: remove hardcoded
            TimePicker(
                initialTimeValue = alarm.time,
                timeFormat = TimeFormat.Hour24Format,
                onValueChange = onUpdateSelectedTime,
                modifier = Modifier.padding(top = 4.dp, bottom = 26.dp, start = 16.dp, end = 16.dp),
                soundEnabled = true,
            )

            RepeatSetting(darkThemeEnabled, alarm.repeatDays, onUpdateRepeatDays)

            LazyColumn {
                item {
                    SettingRow(
                        stringResource(id = R.string.sound),
                        SoundOptionFormatter.getDisplayName(context, alarm.soundOption)
                    ) {
                        onSelectSoundClicked(alarm.soundOption.soundFile)
                    }
                }

                item {
                    SettingRow(stringResource(id = R.string.label), alarm.label) {
                        onShowDialogTypeChanged(DialogType.ALARM_LABEL)
                    }
                }

                item {
                    SettingRow(
                        stringResource(id = R.string.ring_duration),
                        pluralStringResource(
                            id = R.plurals.time_part_minute,
                            count = alarm.ringDurationOption.value,
                            alarm.ringDurationOption.value
                        )
                    ) {
                        onShowDialogTypeChanged(DialogType.RING_DURATION)
                    }
                }

                item {
                    val minutes = pluralStringResource(
                        id = R.plurals.time_part_minute,
                        count = alarm.snoozeOption.duration,
                        alarm.snoozeOption.duration
                    )
                    val times =
                        stringResource(id = R.string.repetition_display, alarm.snoozeOption.number)

                    SettingRow(
                        stringResource(id = R.string.snooze_duration),
                        stringResource(id = R.string.snooze_option_display, minutes, times)
                    ) {
                        onShowDialogTypeChanged(DialogType.SNOOZE_DURATION)
                    }
                }
            }
        }
    }
}

private fun isNewAlarm(id: Long): Boolean {
    return id == DefaultAlarmConfig.NEW_ALARM_ID
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