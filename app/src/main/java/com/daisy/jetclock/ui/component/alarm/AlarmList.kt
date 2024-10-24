package com.daisy.jetclock.ui.component.alarm

import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.daisy.jetclock.domain.Alarm
import com.daisy.jetclock.ui.component.animation.listitemplacement.animatedItemsIndexed
import com.daisy.jetclock.ui.component.animation.listitemplacement.updateAnimatedItemsState
import com.daisy.jetclock.ui.component.animation.swipetoaction.SwipeActions
import com.daisy.jetclock.ui.component.animation.swipetoaction.SwipeActionsConfig
import com.daisy.jetclock.viewmodels.AlarmViewModel


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlarmList(
    viewModel: AlarmViewModel = hiltViewModel(),
    onAlarmClick: (Long) -> Unit,
) {
    val alarmList by viewModel.alarms.collectAsState()

    val animatedList by updateAnimatedItemsState(newList = alarmList) { item -> item.id }

    val enterTransition = remember { expandVertically() }
    val exitTransition = remember { shrinkVertically() }

    val deleteAlarm: (Alarm) -> Unit = remember {
        { item ->
            viewModel.deleteAlarm(item)
        }
    }

    LazyColumn {
        animatedItemsIndexed(
            state = animatedList,
            key = { item -> item.toString() },
            enterTransition = enterTransition,
            exitTransition = exitTransition
        ) { _, item ->
            SwipeActions(
                endActionsConfig = SwipeActionsConfig(
                    threshold = 0.5f,
                    background = MaterialTheme.colors.error,
                    iconTint = MaterialTheme.colors.onError,
                    icon = Icons.Rounded.Delete,
                    stayDismissed = true,
                    onDismiss = { deleteAlarm(item) }
                ),
            ) { state ->
                AlarmColumnContent(
                    item = item,
                    state = state,
                    onAlarmClick = onAlarmClick
                )
            }
        }
    }
}