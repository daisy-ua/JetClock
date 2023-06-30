package com.daisy.jetclock.ui.component.drawable

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.sqrt

sealed interface CirclePathDirection {

    object Center : CirclePathDirection

    class Start(val xOffset: Float) : CirclePathDirection

    class End(val xOffset: Float) : CirclePathDirection
}

class CirclePath(
    private val progress: Float,
    private val xOrigin: CirclePathDirection,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {

        val origin = Offset(
            x = when (xOrigin) {
                is CirclePathDirection.Center -> size.center.x

                is CirclePathDirection.Start -> xOrigin.xOffset

                is CirclePathDirection.End -> size.width - xOrigin.xOffset
            },
            y = size.center.y,
        )

        val radius = (sqrt(
            size.height * size.height + size.width * size.width
        ) * 1f) * progress

        return Outline.Generic(
            Path().apply
            {
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