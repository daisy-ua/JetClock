package com.daisy.jetclock.ui.component.repeatdays

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daisy.jetclock.ui.component.drawable.CirclePath
import com.daisy.jetclock.ui.component.drawable.CirclePathDirection
import com.daisy.jetclock.ui.theme.JetClockTheme

val days = arrayOf("S", "M", "T", "W", "T", "F", "S")

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RepeatDays(
    modifier: Modifier = Modifier,
    darkThemeEnabled: Boolean = isSystemInDarkTheme(),
) {
    val selectedDays = remember {
        mutableStateListOf<Int>()
    }

    val onSelectDays = { isSelected: Boolean, index: Int ->
        if (isSelected) selectedDays.remove(index) else selectedDays.add(index)
    }

    val (selectedFontColor, backgroundColor, backgroundBrush) =
        if (darkThemeEnabled) {
            Triple(
                MaterialTheme.colors.onSurface,
                MaterialTheme.colors.primaryVariant,
                listOf(
                    MaterialTheme.colors.primary,
                    MaterialTheme.colors.primary,
                    MaterialTheme.colors.secondaryVariant,
                ),
            )
        } else {
            Triple(
                MaterialTheme.colors.onPrimary,
                MaterialTheme.colors.surface,
                listOf(
                    MaterialTheme.colors.secondaryVariant,
                    MaterialTheme.colors.secondaryVariant,
                    MaterialTheme.colors.primary,
                ),
            )
        }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .then(modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        items(days.size) { index ->
            val isSelected = selectedDays.contains(index)

            val fontColor by animateColorAsState(
                targetValue = if (isSelected)
                    selectedFontColor
                else MaterialTheme.colors.primary,
                animationSpec = tween(100, easing = LinearEasing),
                label = "",
            )

            AnimatedContent(targetState = isSelected, label = "") { isSelectedState ->
                val revealSize = remember { Animatable(if (isSelectedState) 1f else 0f) }

                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .shadow(2.dp, MaterialTheme.shapes.medium)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            onSelectDays(isSelected, index)
                        }
                        .background(
                            color = backgroundColor,
                            shape = MaterialTheme.shapes.medium,
                        ),
                )
                {
                    Box(
                        modifier = Modifier
                            .clip(
                                CirclePath(
                                    revealSize.value,
                                    CirclePathDirection.Center
                                )
                            )
                            .background(Brush.linearGradient(backgroundBrush))

                    ) {
                        Text(
                            text = days[index],
                            modifier = Modifier
                                .size(40.dp)
                                .wrapContentHeight(),
                            style = MaterialTheme.typography.h6,
                            textAlign = TextAlign.Center,
                            color = fontColor,
                        )
                    }

                    Text(
                        text = days[index],
                        modifier = Modifier
                            .size(40.dp)
                            .wrapContentHeight(),
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.Center,
                        color = fontColor,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RepeatDaysPreview() {
    JetClockTheme(darkTheme = false) {
        RepeatDays(modifier = Modifier.padding(16.dp))
    }
}