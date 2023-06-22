package com.daisy.jetclock.ui.component.timepicker

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs


private const val ITEMS_SIZE = Int.MAX_VALUE

@Composable
fun WheelPicker(
    modifier: Modifier = Modifier,
    items: List<String>,
    firstIndex: Int,
    alignment: Alignment,
    isInfinite: Boolean = true,
    itemWidth: Dp = 50.dp,
    itemHeight: Dp = 30.dp,
    fontSize: TextUnit? = null,
    visibleItemsCount: Int = 5,
) {
    val (itemsSize, startIndex) = if (isInfinite) {
        val itemsMidSize = ITEMS_SIZE / 2 + (firstIndex - 5)
        Pair(ITEMS_SIZE, itemsMidSize)
    } else {
        Pair(items.size, firstIndex)
    }

    val pickerLength = (itemHeight.value * visibleItemsCount).dp

    val listState = rememberLazyListState(startIndex)
    val scope = rememberCoroutineScope()

    val scrollableState = rememberScrollableState { delta ->
        scope.launch {
            listState.scrollBy(-delta * 0.8f)
            listState.animateScrollToItem(index = listState.firstVisibleItemIndex)
        }
        delta
    }

    val selectedItem = remember {
        mutableStateOf("")
    }

    BoxWithConstraints(
        modifier = Modifier
            .height(pickerLength)
            .then(modifier)
    ) {
        val cFontSize = fontSize ?: wheelFontSize(height = itemHeight)
        val halfRowWidth = constraints.maxHeight / 2f

        LazyColumn(
            modifier = Modifier
                .scrollable(
                    orientation = Orientation.Vertical,
                    state = scrollableState
                ),
            userScrollEnabled = false,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = listState,
        ) {
            items(itemsSize) {
                val opacity by remember {
                    calculateDerivedState(listState, it, halfRowWidth)
                }

                ItemContent(
                    items = items,
                    currentIndex = it,
                    opacity = opacity,
                    alignment = alignment,
                    boxHeight = itemHeight,
                    boxWidth = itemWidth,
                    fontSize = cFontSize
                )
            }
        }
    }
}

@Composable
private fun ItemContent(
    items: List<String>,
    currentIndex: Int,
    opacity: Float,
    alignment: Alignment,
    boxHeight: Dp,
    boxWidth: Dp,
    fontSize: TextUnit,
) {
    Box(
        contentAlignment = alignment,
        modifier = Modifier
            .scale(opacity)
            .alpha(opacity)
            .height(boxHeight)
            .width(boxWidth)
    ) {

        val index = currentIndex % items.size

        Text(
            text = items[index],
            modifier = Modifier.align(alignment),
            fontSize = fontSize
        )
    }
}

@Composable
private fun wheelFontSize(height: Dp, padding: Dp = 0.dp): TextUnit {
    return with(LocalDensity.current) {
        ((height * 0.9f).toPx()).toSp()
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
            abs(currentItemInfo.offset + itemHalfSize - halfRowWidth) / halfRowWidth
        ) * 0.8f)
    }
}