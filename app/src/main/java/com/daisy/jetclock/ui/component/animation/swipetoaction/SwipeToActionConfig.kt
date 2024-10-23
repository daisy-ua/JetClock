package com.daisy.jetclock.ui.component.animation.swipetoaction

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

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

internal const val REVEAL_DURATION_MILLIS = 400

internal const val VELOCITY_THRESHOLD = 1000

internal const val OFFSET_BLIND_SIZE = 50f