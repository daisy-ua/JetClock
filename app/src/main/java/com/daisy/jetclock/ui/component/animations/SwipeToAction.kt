package com.daisy.jetclock.ui.component.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.DismissDirection.EndToStart
import androidx.compose.material.DismissDirection.StartToEnd
import androidx.compose.material.DismissValue.DismissedToEnd
import androidx.compose.material.DismissValue.DismissedToStart
import androidx.compose.material.SwipeableDefaults.StandardResistanceFactor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.daisy.jetclock.ui.component.drawable.CirclePath
import com.daisy.jetclock.ui.component.drawable.CirclePathDirection
import com.daisy.jetclock.utils.vibrate
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class SwipeActionsConfig(
    val threshold: Float,
    val icon: ImageVector,
    val iconTint: Color,
    val background: Color,
    val stayDismissed: Boolean,
    val onDismiss: () -> Unit,
)

val DefaultSwipeActionsConfig = SwipeActionsConfig(
    threshold = 0.4f,
    icon = Icons.Default.Delete,
    iconTint = Color.Transparent,
    background = Color.Transparent,
    stayDismissed = false,
    onDismiss = { },
)

private const val REVEAL_DURATION_MILLIS = 400

private const val VELOCITY_THRESHOLD = 1000

private const val OFFSET_BLIND_SIZE = 50f

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

    var willDismissDirection: DismissDirection? by remember {
        mutableStateOf(null)
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

    var isStartConfigActive by remember {
        mutableStateOf(false)
    }

    val willDismiss = willDismissDirection == EndToStart && !isStartConfigActive ||
            willDismissDirection == StartToEnd && isStartConfigActive

    val isStartToEndThresholdPassed: (Float) -> Boolean =
        { it > width * startActionsConfig.threshold }

    val isEndToStartThresholdPassed: (Float) -> Boolean =
        { it < -width * endActionsConfig.threshold }

    val isStartToEndThresholdUndo: (Float) -> Boolean =
        {
            willDismissDirection != null && isStartConfigActive &&
                    it < width * startActionsConfig.threshold && it > OFFSET_BLIND_SIZE
        }

    val isEndToStartThresholdUndo: (Float) -> Boolean =
        {
            willDismissDirection != null && !isStartConfigActive &&
                    it > -width * endActionsConfig.threshold && it < -OFFSET_BLIND_SIZE
        }

    LaunchedEffect(key1 = Unit) {
        snapshotFlow { state.offset.value }
            .collect {
                isStartConfigActive = it > 0

                willDismissDirection = when {
                    isStartToEndThresholdPassed(it) -> StartToEnd

                    isEndToStartThresholdPassed(it) -> EndToStart

                    isEndToStartThresholdUndo(it) -> StartToEnd

                    isStartToEndThresholdUndo(it) -> EndToStart

                    else -> null
                }
            }
    }

    val view = LocalView.current
    LaunchedEffect(key1 = willDismiss) {
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
            SwipeToActionBackground(
                willDismissDirection = willDismissDirection,
                willDismiss = willDismiss,
                isStartConfigActive = isStartConfigActive,
                backgroundColor = backgroundColor,
                startActionsConfig = startActionsConfig,
                endActionsConfig = endActionsConfig
            )
        }
    ) {
        content(state)
    }
}

@Composable
@OptIn(ExperimentalAnimationApi::class)
fun SwipeToActionBackground(
    willDismissDirection: DismissDirection?,
    willDismiss: Boolean,
    isStartConfigActive: Boolean,
    backgroundColor: Color,
    startActionsConfig: SwipeActionsConfig,
    endActionsConfig: SwipeActionsConfig,
) {
    AnimatedContent(
        targetState = Pair(willDismissDirection, willDismiss),
        transitionSpec = {
            fadeIn(
                tween(0),
                initialAlpha = if (targetState.second) 1f else 0f,
            ) with fadeOut(
                tween(0),
                targetAlpha = if (targetState.second) .7f else 0f,
            )
        }
    ) { (direction, willDismiss) ->
        val revealSize = remember { Animatable(if (willDismiss) 0f else 1f) }
        val iconSize = remember { Animatable(1f) }

        LaunchedEffect(key1 = Unit) {

            if (willDismiss) {
                revealSize.snapTo(0f)
                launch {
                    revealSize.animateTo(
                        1f,
                        animationSpec = tween(REVEAL_DURATION_MILLIS)
                    )
                }
                iconSize.snapTo(.8f)
                iconSize.animateTo(
                    1f,
                    spring(
                        dampingRatio = Spring.DampingRatioHighBouncy,
                    )
                )
            } else {
                revealSize.snapTo(1f)
                revealSize.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(REVEAL_DURATION_MILLIS)
                )
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(
                        CirclePath(
                            revealSize.value,
                            if (isStartConfigActive) {
                                CirclePathDirection.Start(constraints.maxHeight.toFloat() / 2)
                            } else {
                                CirclePathDirection.End(constraints.maxHeight.toFloat() / 2)
                            }
                        )
                    )
                    .background(
                        color = when (direction) {
                            StartToEnd ->
                                if (isStartConfigActive) startActionsConfig.background
                                else endActionsConfig.background

                            EndToStart ->
                                if (!isStartConfigActive) endActionsConfig.background
                                else startActionsConfig.background

                            else -> Color.Transparent
                        },
                    )
            )

            Box(
                modifier = Modifier
                    .align(
                        if (isStartConfigActive) Alignment.CenterStart
                        else Alignment.CenterEnd
                    )
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .scale(iconSize.value)
                    .offset {
                        IntOffset(
                            x = 0,
                            y = (10 * (1f - iconSize.value)).roundToInt()
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                if (isStartConfigActive) {
                    Image(
                        painter = rememberVectorPainter(image = startActionsConfig.icon),
                        colorFilter = ColorFilter.tint(startActionsConfig.iconTint),
                        contentDescription = null
                    )
                } else {
                    Image(
                        painter = rememberVectorPainter(image = endActionsConfig.icon),
                        colorFilter = ColorFilter.tint(endActionsConfig.iconTint),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
@ExperimentalMaterialApi
private fun SwipeToDismiss(
    state: DismissState,
    modifier: Modifier = Modifier,
    directions: Set<DismissDirection> = setOf(
        EndToStart,
        StartToEnd
    ),
    dismissThresholds: (DismissDirection) -> ThresholdConfig = { FractionalThreshold(0.5f) },
    background: @Composable RowScope.() -> Unit,
    dismissContent: @Composable RowScope.() -> Unit,
) = BoxWithConstraints(modifier) {
    val width = constraints.maxWidth.toFloat()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val anchors = mutableMapOf(0f to DismissValue.Default)
    if (StartToEnd in directions) anchors += width to DismissedToEnd
    if (EndToStart in directions) anchors += -width to DismissedToStart

    val thresholds = { from: DismissValue, to: DismissValue ->
        dismissThresholds(getDismissDirection(from, to)!!)
    }
    val minFactor =
        if (EndToStart in directions) StandardResistanceFactor else 0f
    val maxFactor =
        if (StartToEnd in directions) StandardResistanceFactor else 0f
    Box(
        Modifier.swipeable(
            state = state,
            anchors = anchors,
            thresholds = thresholds,
            orientation = Orientation.Horizontal,
            enabled = state.currentValue == DismissValue.Default,
            reverseDirection = isRtl,
            resistance = ResistanceConfig(
                basis = width,
                factorAtMin = minFactor,
                factorAtMax = maxFactor
            ),
            velocityThreshold = VELOCITY_THRESHOLD.dp
        )
    ) {
        Row(
            content = background,
            modifier = Modifier.matchParentSize()
        )
        Row(
            content = dismissContent,
            modifier = Modifier.offset { IntOffset(state.offset.value.roundToInt(), 0) }
        )
    }
}

private fun getDismissDirection(from: DismissValue, to: DismissValue): DismissDirection? {
    return when {
        // settled at the default state
        from == to && from == DismissValue.Default -> null
        // has been dismissed to the end
        from == to && from == DismissedToEnd -> StartToEnd
        // has been dismissed to the start
        from == to && from == DismissedToStart -> EndToStart
        // is currently being dismissed to the end
        from == DismissValue.Default && to == DismissedToEnd -> StartToEnd
        // is currently being dismissed to the start
        from == DismissValue.Default && to == DismissedToStart -> EndToStart
        // has been dismissed to the end but is now animated back to default
        from == DismissedToEnd && to == DismissValue.Default -> StartToEnd
        // has been dismissed to the start but is now animated back to default
        from == DismissedToStart && to == DismissValue.Default -> EndToStart
        else -> null
    }
}