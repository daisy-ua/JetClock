package com.daisy.jetclock.ui.component.timepicker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daisy.jetclock.constants.TimeFormat
import com.daisy.jetclock.ui.theme.JetClockTheme

@Composable
fun TimePicker(
    timeFormat: TimeFormat = TimeFormat.Hour12Format,
) {
    val timeFormatter = TimeFormatter.apply { setTimeFormat(timeFormat) }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Spacer(
            modifier = Modifier
                .padding(top = 8.dp, end = 16.dp)
                .height(2.dp)
                .width(50.dp)
                .background(MaterialTheme.colors.onBackground)
                .align(Alignment.CenterVertically)
        )
        WheelPicker(items = timeFormatter.hours, firstIndex = 0, alignment = Alignment.CenterStart)
        Text(":", modifier = Modifier.align(Alignment.CenterVertically), fontSize = 28.sp)
        WheelPicker(items = timeFormatter.minutes, firstIndex = 0, alignment = Alignment.CenterEnd)
        Spacer(
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp)
                .height(2.dp)
                .width(50.dp)
                .background(MaterialTheme.colors.onBackground)
                .align(Alignment.CenterVertically)
        )
    }

}

@Composable
fun WheelPicker(
    items: List<String>,
    firstIndex: Int,
    alignment: Alignment,
) {
    val listState = rememberLazyListState(firstIndex)
    val selectedItem = remember {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = !listState.isScrollInProgress) {
        listState.animateScrollToItem(index = listState.firstVisibleItemIndex)
    }

    BoxWithConstraints(
        modifier = Modifier.height(150.dp)
    ) {
        val halfRowWidth = constraints.maxHeight / 2f

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            state = listState,
        ) {
            items(Int.MAX_VALUE) {
                val opacity by remember {
                    calculateDerivedState(listState, it, halfRowWidth)
                }

                ItemContent(
                    items = items,
                    currentIndex = it,
                    opacity = opacity,
                    alignment = alignment
                )
            }
        }
    }
}

private fun calculateDerivedState(
    listState: LazyListState,
    currentIndex: Int,
    halfRowWidth: Float,
): State<Float> {
    return derivedStateOf {
        val currentItemInfo = listState.layoutInfo.visibleItemsInfo
            .firstOrNull { item -> item.index == currentIndex }
            ?: return@derivedStateOf 0.8f

        val itemHalfSize = currentItemInfo.size / 2


        (1f - minOf(
            1f,
            kotlin.math.abs(currentItemInfo.offset + itemHalfSize - halfRowWidth) / halfRowWidth
        ) * 0.8f)
    }
}

@Composable
private fun ItemContent(
    items: List<String>,
    currentIndex: Int,
    opacity: Float,
    alignment: Alignment,
) {
    Box(
        contentAlignment = alignment,
        modifier = Modifier
            .scale(opacity)
            .alpha(opacity)
            .width(50.dp)
            .height(30.dp)
    ) {

        val index = currentIndex % items.size

        Text(
            text = items[index],
            modifier = Modifier.align(alignment),
            fontSize = 28.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimePickerPreview() {
    JetClockTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            TimePicker()
        }
    }
}