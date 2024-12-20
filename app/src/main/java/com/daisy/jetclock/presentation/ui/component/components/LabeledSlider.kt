package com.daisy.jetclock.presentation.ui.component.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun LabeledSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    steps: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    trackLabel: List<Int>,
    modifier: Modifier = Modifier,
    labelColor: Color = MaterialTheme.colors.onBackground
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        Slider(
            value = value,
            onValueChange = { onValueChange.invoke(it) },
            steps = steps,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                activeTickColor = MaterialTheme.colors.primary
            )
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items(trackLabel) { duration ->
                Text(
                    text = duration.toString(),
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Center,
                    color = labelColor,
                    modifier = Modifier.width(16.dp)
                )
            }
        }
    }
}