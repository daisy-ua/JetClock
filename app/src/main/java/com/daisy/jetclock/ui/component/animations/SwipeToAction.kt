package com.daisy.jetclock.ui.component.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import com.daisy.jetclock.utils.vibrate
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.sqrt

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

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun SwipeActions(
    modifier: Modifier = Modifier,
    startActionsConfig: SwipeActionsConfig = DefaultSwipeActionsConfig,
    endActionsConfig: SwipeActionsConfig = DefaultSwipeActionsConfig,
    content: @Composable (DismissState) -> Unit,
) = BoxWithConstraints(modifier) {
    val width = constraints.maxWidth.toFloat()

    var willDismissDirection: DismissDirection? by remember {
        mutableStateOf(null)
    }

    var isUndoDirection: Boolean by remember {
        mutableStateOf(false)
    }

    val state = rememberDismissState(
        confirmStateChange = {
            when {
                willDismissDirection == DismissDirection.StartToEnd
                        && it == DismissValue.DismissedToEnd -> {
                    startActionsConfig.onDismiss()
                    startActionsConfig.stayDismissed
                }

                willDismissDirection == DismissDirection.EndToStart
                        && it == DismissValue.DismissedToStart -> {
                    endActionsConfig.onDismiss()
                    endActionsConfig.stayDismissed
                }

                else -> false
            }
        }
    )

    LaunchedEffect(key1 = Unit, block = {
        snapshotFlow { state.offset.value }
            .collect {
                willDismissDirection = when {
                    it > width * startActionsConfig.threshold -> DismissDirection.StartToEnd
                    it < -width * endActionsConfig.threshold -> DismissDirection.EndToStart
                    it == 0f -> {
                        isUndoDirection = false
                        null
                    }
                    else -> {
                        isUndoDirection = true
                        null
                    }
                }
            }
    })

    val view = LocalView.current
    LaunchedEffect(key1 = willDismissDirection, block = {
        if (willDismissDirection != null || isUndoDirection) {
            view.vibrate()
        }
    })

    val dismissDirections by remember(startActionsConfig, endActionsConfig) {
        derivedStateOf {
            mutableSetOf<DismissDirection>().apply {
                if (startActionsConfig != DefaultSwipeActionsConfig) add(DismissDirection.StartToEnd)
                if (endActionsConfig != DefaultSwipeActionsConfig) add(DismissDirection.EndToStart)
            }
        }
    }

    SwipeToDismiss(
        state = state,
        directions = dismissDirections,
        dismissThresholds = {
            if (it == DismissDirection.StartToEnd)
                FractionalThreshold(startActionsConfig.threshold)
            else FractionalThreshold(endActionsConfig.threshold)
        },
        background = {
            AnimatedContent(
                targetState = Pair(state.dismissDirection, willDismissDirection != null),
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
                val iconSize = remember { Animatable(if (willDismiss) .4f else .8f) }
                LaunchedEffect(key1 = Unit, block = {
                    if (willDismiss) {
                        revealSize.snapTo(0f)
                        launch {
                            revealSize.animateTo(1f, animationSpec = tween(600))
                        }
                        iconSize.snapTo(.8f)
                        iconSize.animateTo(
                            1.2f,
                            spring(
                                dampingRatio = Spring.DampingRatioHighBouncy,
                            )
                        )
                        iconSize.animateTo(
                            1f,
                            spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                            )
                        )
                    }
                })
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.primary)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CirclePath(
                                revealSize.value,
                                direction == DismissDirection.StartToEnd
                            ))
                            .background(
                                color = when (direction) {
                                    DismissDirection.StartToEnd ->
                                        if (willDismiss) startActionsConfig.background
                                        else startActionsConfig.iconTint

                                    DismissDirection.EndToStart ->
                                        if (willDismiss) endActionsConfig.background
                                        else MaterialTheme.colors.primary

                                    else -> Color.Transparent
                                },
                            )
                    ) {
                        Box(modifier = Modifier
                            .align(
                                when (direction) {
                                    DismissDirection.StartToEnd -> Alignment.CenterStart
                                    else -> Alignment.CenterEnd
                                }
                            )
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .scale(iconSize.value)
                            .offset {
                                IntOffset(x = 0,
                                    y = (10 * (1f - iconSize.value)).roundToInt())
                            },
                            contentAlignment = Alignment.Center
                        ) {
                            when (direction) {
                                DismissDirection.StartToEnd -> {
                                    Image(
                                        painter = rememberVectorPainter(image = startActionsConfig.icon),
                                        colorFilter = ColorFilter.tint(
                                            if (willDismiss) startActionsConfig.iconTint
                                            else startActionsConfig.background
                                        ),
                                        contentDescription = null
                                    )
                                }
                                DismissDirection.EndToStart -> {
                                    Image(
                                        painter = rememberVectorPainter(image = endActionsConfig.icon),
                                        colorFilter = ColorFilter.tint(
                                            if (willDismiss) endActionsConfig.iconTint
                                            else endActionsConfig.background
                                        ),
                                        contentDescription = null
                                    )
                                }
                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    ) {
        content(state)
    }
}

class CirclePath(private val progress: Float, private val start: Boolean) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {

        val origin = Offset(
            x = if (start) 0f else size.width,
            y = size.center.y,
        )

        val radius = (sqrt(
            size.height * size.height + size.width * size.width
        ) * 1f) * progress

        return Outline.Generic(
            Path().apply {
                addOval(
                    Rect(
                        center = origin,
                        radius = radius,
                    )
                )
            }
        )
    }
}
