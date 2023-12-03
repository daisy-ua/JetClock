package com.daisy.jetclock.ui.screens

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daisy.jetclock.ui.component.components.ListRowComponent
import com.daisy.jetclock.ui.component.repeatdays.RepeatDays
import com.daisy.jetclock.ui.component.scaffold.JetClockFuncTopAppBar
import com.daisy.jetclock.ui.component.scaffold.TextFloatingActionButton
import com.daisy.jetclock.ui.component.timepicker.TimePicker
import com.daisy.jetclock.ui.theme.JetClockTheme

@Composable
fun SetAlarmScreen() {
    Scaffold(
        topBar = { JetClockFuncTopAppBar(title = "Set alarm") },
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

            RepeatSetting()

            LazyColumn {
                item {
                    SettingRow("Sound", "Default")
                }

                item {
                    SettingRow("Label", "Alarm")
                }

                item {
                    SettingRow("Ring duration", "5 minutes")
                }

                item {
                    SettingRow("Snooze duration", "10 minutes, 3x")
                }
            }
        }
    }
}

@Composable
fun SettingRow(option: String, value: String) {
    ListRowComponent(
        onItemClick = { },
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
            color = MaterialTheme.colors.onBackground.copy(0.5f),
        )
    }
}

@Composable
fun RepeatSetting() {
    Text(
        text = "Repeat",
        modifier = Modifier.padding(start = 20.dp, top = 8.dp),
        style = MaterialTheme.typography.body1,
        color = MaterialTheme.colors.onBackground,
        fontWeight = FontWeight.Medium
    )

    RepeatDays(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun SetAlarmScreenPreview() {
    JetClockTheme(darkTheme = false) {
        SetAlarmScreen()
    }
}