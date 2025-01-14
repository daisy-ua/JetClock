package com.daisy.jetclock.presentation.ui.component.button

import androidx.compose.foundation.layout.Box
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableButton(
    modifier: Modifier = Modifier,
    dismissThreshold: Float = 0.4f,
    dismissDirections: Set<DismissDirection> = setOf(
        DismissDirection.StartToEnd,
        DismissDirection.EndToStart
    ),
    action: () -> Unit,
    dismissState: DismissState = rememberDismissState(
        confirmStateChange = { state ->
            if (state == DismissValue.DismissedToStart || state == DismissValue.DismissedToEnd) {
                action()
            }
            true
        }
    ),
    background: @Composable (DismissDirection?) -> Unit = {},
    content: @Composable () -> Unit,
) {
    SwipeToDismiss(
        state = dismissState,
        directions = dismissDirections,
        dismissThresholds = { FractionalThreshold(dismissThreshold) },
        background = { background(dismissState.dismissDirection) },
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

