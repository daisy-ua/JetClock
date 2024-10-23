package com.daisy.jetclock.ui.component.animation.swipetoaction

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissDirection.EndToStart
import androidx.compose.material.DismissDirection.StartToEnd
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue.DismissedToEnd
import androidx.compose.material.DismissValue.DismissedToStart
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.MaterialTheme
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import com.daisy.jetclock.utils.vibrate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeActions(
    modifier: Modifier = Modifier,
    startActionsConfig: SwipeActionsConfig = DefaultSwipeActionsConfig,
    endActionsConfig: SwipeActionsConfig = DefaultSwipeActionsConfig,
    backgroundColor: Color = MaterialTheme.colors.primary,
    content: @Composable (DismissState) -> Unit,
) = BoxWithConstraints(modifier) {
    val width = constraints.maxWidth.toFloat()

    val startThreshold = width * startActionsConfig.threshold
    val endThreshold = -width * endActionsConfig.threshold

    var willDismissDirection: DismissDirection? by remember {
        mutableStateOf(null)
    }

    var isStartConfigActive by remember {
        mutableStateOf(false)
    }

    val state = rememberDismissState(
        confirmStateChange = {
            when {
                willDismissDirection == StartToEnd && it == DismissedToEnd -> {
                    startActionsConfig.onDismiss()
                    startActionsConfig.stayDismissed
                }

                willDismissDirection == EndToStart && it == DismissedToStart -> {
                    endActionsConfig.onDismiss()
                    endActionsConfig.stayDismissed
                }

                else -> true
            }
        }
    )

    when {
        state.isDismissed(StartToEnd) -> startActionsConfig.onDismiss()
        state.isDismissed(EndToStart) -> endActionsConfig.onDismiss()
    }

    val isStartToEndThresholdPassed: (Float) -> Boolean = remember { { it > startThreshold } }
    val isEndToStartThresholdPassed: (Float) -> Boolean = remember { { it < endThreshold } }

    val isStartToEndThresholdUndo: (Float) -> Boolean =
        remember(willDismissDirection, isStartConfigActive, endThreshold) {
            {
                willDismissDirection != null && isStartConfigActive &&
                        it < startThreshold && it > OFFSET_BLIND_SIZE
            }
        }

    val isEndToStartThresholdUndo: (Float) -> Boolean =
        remember(willDismissDirection, isStartConfigActive, endThreshold) {
            {
                willDismissDirection != null && !isStartConfigActive &&
                        it > endThreshold && it < -OFFSET_BLIND_SIZE
            }
        }

    var shouldLoadBackground by remember {
        mutableStateOf(false)
    }

    val enableBackgroundLoading: () -> Unit = remember {
        {
            if (!shouldLoadBackground) {
                shouldLoadBackground = true
            }
        }
    }

    val determineState: (Float) -> Pair<Boolean, DismissDirection?> = { offset ->
        val isStartActive = offset > 0

        val newDirection = when {
            isStartToEndThresholdPassed(offset) -> StartToEnd

            isEndToStartThresholdPassed(offset) -> EndToStart

            isEndToStartThresholdUndo(offset) -> StartToEnd

            isStartToEndThresholdUndo(offset) -> EndToStart

            else -> null
        }

        Pair(isStartActive, newDirection)
    }

    LaunchedEffect(key1 = Unit) {
        snapshotFlow { state.offset.value }
            .mapLatest { offset ->
                if (offset != 0f) {
                    enableBackgroundLoading()
                }
                withContext(Dispatchers.Default) {
                    determineState(offset)
                }
            }
            .collect { (isStartActive, newDirection) ->
                isStartConfigActive = isStartActive
                willDismissDirection = newDirection
            }
    }

    val isThresholdPassed by remember {
        derivedStateOf {
            willDismissDirection == EndToStart && !isStartConfigActive ||
                    willDismissDirection == StartToEnd && isStartConfigActive
        }
    }

    val view = LocalView.current
    LaunchedEffect(key1 = isThresholdPassed) {
        if (willDismissDirection != null) {
            view.vibrate()
        }
    }

    val dismissDirections by remember(startActionsConfig, endActionsConfig) {
        derivedStateOf {
            mutableSetOf<DismissDirection>().apply {
                if (startActionsConfig != DefaultSwipeActionsConfig) add(StartToEnd)
                if (endActionsConfig != DefaultSwipeActionsConfig) add(EndToStart)
            }
        }
    }

    SwipeToDismiss(
        state = state,
        directions = dismissDirections,
        dismissThresholds = {
            if (it == StartToEnd)
                FractionalThreshold(startActionsConfig.threshold)
            else FractionalThreshold(endActionsConfig.threshold)
        },
        background = {
            if (shouldLoadBackground) {
                SwipeToActionBackground(
                    willDismissDirection = willDismissDirection,
                    willDismiss = isThresholdPassed,
                    isStartConfigActive = isStartConfigActive,
                    backgroundColor = backgroundColor,
                    startActionsConfig = startActionsConfig,
                    endActionsConfig = endActionsConfig,
                )
            }
        }
    ) {
        content(state)
    }
}