package com.daisy.jetclock.ui.component.drawable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RoundedLine(
    modifier: Modifier = Modifier,
    width: Dp = 50.dp,
    height: Dp = 2.dp,
    colors: List<Color> = listOf(
        MaterialTheme.colors.primaryVariant,
        MaterialTheme.colors.secondaryVariant,
        MaterialTheme.colors.primary,
    ),
) {
    Canvas(
        modifier = Modifier
            .then(modifier)
            .width(width)
            .height(height)
    ) {
        drawLine(
            brush = Brush.linearGradient(colors),
            start = Offset.Zero,
            end = Offset(x = size.width, y = 0f),
            strokeWidth = size.height,
            cap = StrokeCap.Round
        )
    }
}