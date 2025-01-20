package com.daisy.jetclock.presentation.ui.component.alarm

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.daisy.jetclock.constants.DefaultAlarmConfig
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.presentation.ui.theme.AfricanViolet
import com.daisy.jetclock.presentation.ui.theme.Platinum
import com.daisy.jetclock.presentation.ui.theme.PortGore
import com.daisy.jetclock.presentation.ui.theme.UltraViolet


@Composable
fun NextAlarmCard(
    alarm: Alarm?,
    alarmTime: String,
    ringInTime: String,
    onClick: (Long) -> Unit,
    onStartRefreshing: () -> Unit,
    onStopRefreshing: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            onStartRefreshing()
        }
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            onStopRefreshing()
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
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
                    onClick(alarm?.id ?: DefaultAlarmConfig.NEW_ALARM_ID)
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
                        text = alarm?.time?.meridiem?.name ?: "",
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
                        .padding(start = 3.dp, top = 8.dp)
                        .align(CenterHorizontally),
                )
            }
        }
    }
}