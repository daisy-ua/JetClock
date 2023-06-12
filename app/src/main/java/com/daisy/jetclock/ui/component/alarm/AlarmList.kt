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
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.daisy.jetclock.ui.component.animations.SwipeActions
import com.daisy.jetclock.ui.component.animations.SwipeActionsConfig
import com.daisy.jetclock.ui.component.animations.animatedItemsIndexed
import com.daisy.jetclock.ui.component.animations.updateAnimatedItemsState
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlarmList() {

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
                    background = Color.Red,
                    iconTint = Color.White,
                    icon = Icons.Rounded.Delete,
                    stayDismissed = true,
                    onDismiss = {
                        alarmList.removeIf {
                            it.id == item.id
                        }
                    }
                ),
            ) { state -> AlarmColumnContent(state = state) }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlarmColumnContent(state: DismissState) {
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
        }
    )
    val elevation by animateDpAsState(
        targetValue = when {
            animateCorners -> 6.dp
            else -> 2.dp
        }
    )

    Card(
        backgroundColor = MaterialTheme.colors.background,
        shape = RoundedCornerShape(
            topEnd = endCorners,
            bottomEnd = endCorners,
        ),
        elevation = elevation,
    ) {
        AlarmCard()
    }
}