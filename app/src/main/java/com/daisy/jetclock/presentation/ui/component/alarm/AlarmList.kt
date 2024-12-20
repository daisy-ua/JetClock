package com.daisy.jetclock.presentation.ui.component.alarm

import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.presentation.ui.component.alarm.AlarmColumnContent
import com.daisy.jetclock.presentation.ui.component.animation.listitemplacement.animatedItemsIndexed
import com.daisy.jetclock.presentation.ui.component.animation.listitemplacement.updateAnimatedItemsState
import com.daisy.jetclock.presentation.ui.component.animation.swipetoaction.SwipeActions
import com.daisy.jetclock.presentation.ui.component.animation.swipetoaction.SwipeActionsConfig


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlarmList(
    alarmList: List<Alarm>,
    onAlarmClick: (Long) -> Unit,
    onAlarmDelete: (Alarm) -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedList by updateAnimatedItemsState(newList = alarmList) { item -> item.id }

    val enterTransition = remember { expandVertically() }
    val exitTransition = remember { shrinkVertically() }

    LazyColumn(
        modifier = modifier
    ) {
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
                    onDismiss = { onAlarmDelete(item) }
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