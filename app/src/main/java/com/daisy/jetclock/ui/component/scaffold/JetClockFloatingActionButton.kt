package com.daisy.jetclock.ui.component.scaffold

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.daisy.jetclock.ui.theme.JetClockTheme

@Composable
fun JetClockFloatingActionButton() {
    if (MaterialTheme.colors.isLight) {
        IconButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .size(46.dp)
                .drawColoredShadow(
                    color = MaterialTheme.colors.secondaryVariant,
                    alpha = 0.6f,
                    borderRadius = 100.dp,
                    shadowRadius = 12.dp,
                    offsetY = 6.dp,
                    horizontalPadding = 8f,
                    topPadding = 4f
                )
                .background(
                    color = MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(26.dp)
                )
        ) {
            AddIcon(
                colors = listOf(
                    MaterialTheme.colors.primaryVariant,
                    MaterialTheme.colors.primary,
                    MaterialTheme.colors.secondaryVariant
                ),
            )
        }
    } else {
        IconButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .size(46.dp)
                .drawColoredShadow(
                    color = MaterialTheme.colors.secondaryVariant,
                    alpha = 0.4f,
                    borderRadius = 100.dp,
                    shadowRadius = 22.dp,
                    offsetY = 6.dp,
                    horizontalPadding = 8f,
                    topPadding = 14f
                )
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colors.primaryVariant,
                            MaterialTheme.colors.primary,
                            MaterialTheme.colors.secondaryVariant,
                        )
                    ),
                    shape = RoundedCornerShape(26.dp),
                ),
        ) {
            AddIcon(color = MaterialTheme.colors.onBackground)
        }
    }
}

fun Modifier.drawColoredShadow(
    color: Color,
    alpha: Float = 0.2f,
    borderRadius: Dp = 0.dp,
    shadowRadius: Dp = 20.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    horizontalPadding: Float = 0f,
    topPadding: Float = 0f,
) = this.drawBehind {
    val transparentColor =
        android.graphics.Color.toArgb(color.copy(alpha = 0.0f).value.toLong())
    val shadowColor = android.graphics.Color.toArgb(color.copy(alpha = alpha).value.toLong())
    this.drawIntoCanvas {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparentColor
        frameworkPaint.setShadowLayer(
            shadowRadius.toPx(),
            offsetX.toPx(),
            offsetY.toPx(),
            shadowColor
        )
        it.drawRoundRect(
            0f + horizontalPadding,
            0f + topPadding,
            this.size.width - horizontalPadding,
            this.size.height,
            borderRadius.toPx(),
            borderRadius.toPx(),
            paint
        )
    }
}

@Composable
fun AddIcon(
    iconSize: Dp = 24.dp,
    strokeWidth: Float = 7f,
    colors: List<Color>,
) {
    Canvas(
        modifier = Modifier
            .size(iconSize)
            .padding(4.dp)
    ) {
        val centerX = size.width * .5f
        val centerY = size.height * .5f

        drawLine(
            brush = Brush.horizontalGradient(colors),
            start = Offset(x = 0f, y = centerY),
            end = Offset(x = size.width, y = centerY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            brush = Brush.verticalGradient(colors),
            start = Offset(x = centerX, y = 0f),
            end = Offset(x = centerX, y = size.height),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun AddIcon(
    iconSize: Dp = 24.dp,
    strokeWidth: Float = 7f,
    color: Color,
) {
    Canvas(
        modifier = Modifier
            .size(iconSize)
            .padding(4.dp)
    ) {
        val centerX = size.width * .5f
        val centerY = size.height * .5f

        drawLine(
            color = color,
            start = Offset(x = 0f, y = centerY),
            end = Offset(x = size.width, y = centerY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(x = centerX, y = 0f),
            end = Offset(x = centerX, y = size.height),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true)
@Composable
fun JetClockFloatingActionButtonPreview() {
    JetClockTheme {
        JetClockFloatingActionButton()
    }
}
