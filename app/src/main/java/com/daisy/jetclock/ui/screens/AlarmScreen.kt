package com.daisy.jetclock.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daisy.jetclock.ui.component.alarm.AlarmList
import com.daisy.jetclock.ui.component.alarm.NextAlarmCard
import com.daisy.jetclock.ui.component.scaffold.JetClockTopAppBar

@Composable
fun AlarmScreen() {
    Scaffold(
        topBar = { JetClockTopAppBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.size(46.dp),
            ) {
                Icon(Icons.Filled.Add, "Add new alarm",
                    modifier = Modifier.size(28.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) {
        Column {
            NextAlarmCard()
            AlarmList()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlarmScreenPreview() {
    AlarmScreen()
}
