/*
 * Copyright 2023 daisy-ua
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Portions of this code are derived from The Android Open Source Project.
 * Copyright 2020 The Android Open Source Project.
 */


package com.daisy.jetclock.ui.component.animation.swipetoaction

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissDirection.EndToStart
import androidx.compose.material.DismissDirection.StartToEnd
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.DismissValue.DismissedToEnd
import androidx.compose.material.DismissValue.DismissedToStart
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.ResistanceConfig
import androidx.compose.material.SwipeableDefaults.StandardResistanceFactor
import androidx.compose.material.ThresholdConfig
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


@Composable
@ExperimentalMaterialApi
internal fun SwipeToDismiss(
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