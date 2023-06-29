package com.daisy.jetclock.ui.component.repeatdays

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daisy.jetclock.ui.theme.JetClockTheme

val days = arrayOf("S", "M", "T", "W", "T", "F", "S")

@Composable
fun RepeatDays(
    modifier: Modifier = Modifier,
) {
    val selectedDays = remember {
        mutableStateListOf<Int>()
    }

    val onSelectDays = { isSelected: Boolean, index: Int ->
        if (isSelected) selectedDays.remove(index) else selectedDays.add(index)
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

            val fontColor =
                if (isSelected)
                    MaterialTheme.colors.onPrimary
                else MaterialTheme.colors.primary

            Card(
                elevation = 2.dp,
                modifier = Modifier
                    .padding(2.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        onSelectDays(isSelected, index)
                    }
            ) {
                Box(
                    modifier = Modifier
                        .then(
                            if (isSelected) Modifier.background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colors.secondaryVariant,
                                        MaterialTheme.colors.secondaryVariant,
                                        MaterialTheme.colors.primary,
                                    ),
                                )
                            ) else Modifier.background(MaterialTheme.colors.surface)
                        )
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
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RepeatDaysPreview() {
    JetClockTheme {
        RepeatDays(modifier = Modifier.padding(16.dp))
    }
}