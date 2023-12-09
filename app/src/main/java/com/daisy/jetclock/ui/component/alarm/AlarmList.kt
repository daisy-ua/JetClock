package com.daisy.jetclock.ui.component.alarm

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.daisy.jetclock.ui.component.animations.SwipeActions
import com.daisy.jetclock.ui.component.animations.SwipeActionsConfig
import com.daisy.jetclock.ui.component.animations.animatedItemsIndexed
import com.daisy.jetclock.ui.component.animations.updateAnimatedItemsState
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlarmList(
    onAlarmClick: (Int) -> Unit,
) {

    data class Alarm(
        val id: Int,
        val title: String = "Alarm $id",
    )

    val alarmList = remember {
        mutableStateListOf<Alarm>().also { list ->
            for (id in 0..5) {
                list.add(Alarm(id))
            }
        }
    }

    val animatedList by updateAnimatedItemsState(newList = alarmList.map { it })

    LazyColumn {
        animatedItemsIndexed(
            state = animatedList,
            key = { item -> item.id }
        ) { _, item ->
            SwipeActions(
                endActionsConfig = SwipeActionsConfig(
                    threshold = 0.5f,
                    background = MaterialTheme.colors.error,
                    iconTint = MaterialTheme.colors.onError,
                    icon = Icons.Rounded.Delete,
                    stayDismissed = true,
                    onDismiss = {
                        alarmList.removeIf {
                            it.id == item.id
                        }
                    }
                ),
            ) { state -> AlarmColumnContent(state = state, onAlarmClick = onAlarmClick) }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlarmColumnContent(
    state: DismissState,
    onAlarmClick: (Int) -> Unit,
) {
    val animateCorners by remember {
        derivedStateOf {
            state.offset.value.absoluteValue > 30
        }
    }
    val endCorners by animateDpAsState(
        targetValue = when {
            state.dismissDirection == DismissDirection.EndToStart &&
                    animateCorners -> 8.dp

            else -> 0.dp
        }, label = ""
    )
    val elevation by animateDpAsState(
        targetValue = when {
            animateCorners -> 6.dp
            else -> 2.dp
        }, label = ""
    )

    Card(
        backgroundColor = MaterialTheme.colors.background,
        shape = RoundedCornerShape(
            topEnd = endCorners,
            bottomEnd = endCorners,
        ),
        elevation = elevation,
    ) {
        AlarmCard(onAlarmClick = onAlarmClick)
    }
}