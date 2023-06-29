package com.daisy.jetclock.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daisy.jetclock.ui.component.repeatdays.RepeatDays
import com.daisy.jetclock.ui.component.scaffold.JetClockFuncTopAppBar
import com.daisy.jetclock.ui.component.scaffold.drawColoredShadow
import com.daisy.jetclock.ui.component.timepicker.TimePicker
import com.daisy.jetclock.ui.theme.JetClockTheme

@Composable
fun SetAlarmScreen() {
    Scaffold(
        topBar = { JetClockFuncTopAppBar(title = "Set alarm") },
        floatingActionButton = { DeleteFloatingActionButton() },
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
    Card(
        backgroundColor = MaterialTheme.colors.background,
        shape = RoundedCornerShape(0.dp),
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
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

@Composable
fun DeleteFloatingActionButton() {
    Box(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .drawColoredShadow(
                color = MaterialTheme.colors.secondaryVariant,
//                alpha = 0.6f,
                borderRadius = 0.dp,
//                shadowRadius = 0.dp,
//                offsetY = 6.dp,
//                horizontalPadding = 8f,
//                topPadding = 4f
            )
            .clickable { /*TODO*/ }

            .background(
                color = Color.Red,
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = 30.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Delete".uppercase(),
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SetAlarmScreenPreview() {
    JetClockTheme(darkTheme = false) {
        SetAlarmScreen()
    }
}