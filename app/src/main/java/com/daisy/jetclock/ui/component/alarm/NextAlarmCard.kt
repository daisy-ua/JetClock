package com.daisy.jetclock.ui.component.alarm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.daisy.jetclock.constants.NewAlarmDefaults
import com.daisy.jetclock.domain.Alarm
import com.daisy.jetclock.ui.theme.AfricanViolet
import com.daisy.jetclock.ui.theme.Platinum
import com.daisy.jetclock.ui.theme.PortGore
import com.daisy.jetclock.ui.theme.UltraViolet
import com.daisy.jetclock.viewmodels.AlarmViewModel

@Composable
fun NextAlarmCard(
    viewModel: AlarmViewModel = hiltViewModel(),
    onClick: (Long) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val nextAlarm by viewModel.nextAlarm.collectAsState()
    val nextAlarmTime by viewModel.nextAlarmTime.collectAsState()

    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.startRefreshingNextAlarmTime()
        }
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            viewModel.stopRefreshingNextAlarmTime()
        }
    }

    NextAlarmCardContent(
        alarm = nextAlarm,
        alarmTime = nextAlarm?.let { viewModel.getTimeString(it.hour, it.minute) } ?: "",
        ringInTime = nextAlarmTime ?: "No alarm scheduled.",
        onClick = onClick
    )
}

@Composable
fun NextAlarmCardContent(
    alarm: Alarm?,
    alarmTime: String,
    ringInTime: String,
    onClick: (Long) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = 4.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            PortGore, UltraViolet, AfricanViolet
                        ),
                    )
                )
                .clickable {
                    onClick(alarm?.id ?: NewAlarmDefaults.NEW_ALARM_ID)
                }
                .padding(horizontal = 20.dp, vertical = 28.dp)
                .fillMaxWidth()
        ) {
            Column {
                Row {
                    Text(
                        text = alarmTime,
                        style = MaterialTheme.typography.h4,
                        fontWeight = FontWeight.Bold,
                        color = Platinum
                    )
                    Text(
                        text = alarm?.meridiem?.name ?: "",
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold,
                        color = Platinum,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Text(
                    text = ringInTime,
                    style = MaterialTheme.typography.subtitle1,
                    color = Platinum,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(CenterHorizontally),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NextAlarmCardPreview() {
    NextAlarmCard(onClick = {})
}