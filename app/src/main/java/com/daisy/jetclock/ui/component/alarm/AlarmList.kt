package com.daisy.jetclock.ui.component.alarm

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AlarmList() {
    LazyColumn {
        items(9) { index ->
            AlarmCard()
            if (index < 8) {
                Divider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    thickness = 1.dp,
                )
            }
        }
    }
}
