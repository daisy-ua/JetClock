package com.daisy.jetclock.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.daisy.jetclock.constants.NewAlarmDefaults
import com.daisy.jetclock.ui.component.alarm.AlarmList
import com.daisy.jetclock.ui.component.alarm.NextAlarmCard
import com.daisy.jetclock.ui.component.scaffold.JetClockFloatingActionButton
import com.daisy.jetclock.ui.component.scaffold.JetClockTopAppBar
import com.daisy.jetclock.ui.theme.JetClockTheme
import com.daisy.jetclock.viewmodels.AlarmViewModel

@Composable
fun AlarmScreen(
    onAlarmClick: (Long) -> Unit,
    viewModel: AlarmViewModel = hiltViewModel<AlarmViewModel>(),
) {
    Scaffold(
        topBar = { JetClockTopAppBar() },
        floatingActionButton = {
            JetClockFloatingActionButton {
                onAlarmClick(NewAlarmDefaults.NEW_ALARM_ID)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) {
        Column {
            NextAlarmCard()
            AlarmList(viewModel, onAlarmClick)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlarmScreenPreview() {
    JetClockTheme {
//        AlarmScreen({})
    }
}
