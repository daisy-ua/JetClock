package com.daisy.jetclock.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.daisy.jetclock.ui.component.alarm.AlarmList
import com.daisy.jetclock.ui.component.alarm.NextAlarmCard
import com.daisy.jetclock.ui.component.scaffold.JetClockFloatingActionButton
import com.daisy.jetclock.ui.component.scaffold.JetClockTopAppBar
import com.daisy.jetclock.ui.theme.JetClockTheme

@Composable
fun AlarmScreen(
    onAlarmClick: (Int) -> Unit,
) {
    Scaffold(
        topBar = { JetClockTopAppBar() },
        floatingActionButton = { JetClockFloatingActionButton() },
        floatingActionButtonPosition = FabPosition.Center,
    ) {
        Column {
            NextAlarmCard()
            AlarmList(onAlarmClick)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlarmScreenPreview() {
    JetClockTheme {
        AlarmScreen({})
    }
}
