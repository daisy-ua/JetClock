package com.daisy.jetclock.presentation.ui.component.animation.swipetoaction

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissDirection.EndToStart
import androidx.compose.material.DismissDirection.StartToEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.IntOffset
import com.daisy.jetclock.presentation.ui.component.drawable.CirclePath
import com.daisy.jetclock.presentation.ui.component.drawable.CirclePathDirection
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
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
            ) togetherWith fadeOut(
                tween(0),
                targetAlpha = if (targetState.second) .7f else 0f,
            )
        }, label = ""
    ) { (direction, isThresholdPasses) ->
        val revealSize = remember { Animatable(if (isThresholdPasses) 0f else 1f) }
        val iconSize = remember { Animatable(1f) }

        LaunchedEffect(key1 = Unit) {
            if (isThresholdPasses) {
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