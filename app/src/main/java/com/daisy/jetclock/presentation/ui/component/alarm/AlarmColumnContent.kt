package com.daisy.jetclock.presentation.ui.component.alarm

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daisy.jetclock.domain.model.Alarm
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlarmColumnContent(
    item: Alarm,
    state: DismissState,
    onAlarmClick: (Long) -> Unit,
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
        }, label = "elevationAnimation"
    )

    Card(
        modifier = Modifier
            .clickable {
                onAlarmClick(item.id)
            },
        backgroundColor = MaterialTheme.colors.background,
        shape = RoundedCornerShape(
            topEnd = endCorners,
            bottomEnd = endCorners,
        ),
        elevation = elevation,
    ) {
        AlarmCard(item)
    }
}