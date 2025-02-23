package com.daisy.jetclock.presentation.ui.component.timepicker

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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.daisy.jetclock.R
import com.daisy.jetclock.utils.SoundPoolManager
import kotlinx.coroutines.launch
import kotlin.math.abs


private const val ITEMS_SIZE = Int.MAX_VALUE

@Composable
fun WheelPicker(
    items: List<String>,
    initialIndex: Int,
    onValueChange: (Int) -> Unit,
    alignment: Alignment,
    modifier: Modifier = Modifier,
    isInfinite: Boolean = true,
    itemWidth: Dp = 50.dp,
    itemHeight: Dp = 30.dp,
    fontSize: TextUnit? = null,
    fontColor: Color = MaterialTheme.colors.primaryVariant,
    visibleItemsCount: Int = 5,
    soundEnabled: Boolean = true,
) {
    var isInitialized by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(initialIndex) }

    fun onUserSelect(index: Int) {
        if (isInitialized) {
            onValueChange(index)
        } else {
            isInitialized = true
        }
        selectedIndex = index
    }

    val (itemsSize, targetIndex) = calculateTargetValues(
        isInfinite, items.size, initialIndex, visibleItemsCount
    )

    val pickerLength by remember {
        mutableStateOf((itemHeight.value * visibleItemsCount).dp)
    }

    val listState = rememberLazyListState(targetIndex)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val soundPoolManager = remember {
        if (soundEnabled) SoundPoolManager().also {
            it.setSoundId(
                context,
                R.raw.gate_latch_click_1924
            )
        } else null
    }

    var previousIndex by remember { mutableIntStateOf(0) }

    val scrollableState = rememberScrollableState { delta ->
        scope.launch {
            listState.scrollBy(-delta * 0.4f)
            val currentIndex = (listState.firstVisibleItemIndex + 1)

            soundPoolManager?.let { manager ->
                if (currentIndex != previousIndex) {
                    previousIndex = currentIndex
                    manager.playSound(rate = 5f)
                }
            }
        }
        delta
    }

    LaunchedEffect(scrollableState.isScrollInProgress) {
        if (!scrollableState.isScrollInProgress) {
            val currentIndex =
                (listState.firstVisibleItemIndex + visibleItemsCount / 2) % items.size
            onUserSelect(currentIndex)
        }
    }

    LaunchedEffect(!scrollableState.isScrollInProgress) {
        listState.animateScrollToItem(index = listState.firstVisibleItemIndex)
    }

    LaunchedEffect(targetIndex) {
        if (listState.firstVisibleItemIndex != targetIndex) {
            listState.scrollToItem(index = targetIndex)
        }
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
                    fontSize = cFontSize,
                    fontColor = fontColor,
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
    fontColor: Color,
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
            fontSize = fontSize,
            color = fontColor
        )
    }
}

@Composable
private fun wheelFontSize(height: Dp): TextUnit {
    return with(LocalDensity.current) {
        ((height * 0.9f).toPx()).toSp()
    }
}

private fun calculateTargetValues(
    isInfinite: Boolean,
    itemsSize: Int,
    initialIndex: Int,
    visibleItemsCount: Int,
): Pair<Int, Int> {
    return if (isInfinite) {
        calculateInfiniteTargetValues(
            itemsSize = itemsSize,
            initialIndex = initialIndex,
            visibleItemsCount = visibleItemsCount
        )
    } else {
        Pair(itemsSize, initialIndex)
    }
}

private fun calculateInfiniteTargetValues(
    itemsSize: Int,
    initialIndex: Int,
    visibleItemsCount: Int,
): Pair<Int, Int> {
    val listMidIndex = ITEMS_SIZE / 2 - 1
    val itemsMidIndex = listMidIndex % itemsSize
    val targetIndex = if (initialIndex < itemsMidIndex) {
        listMidIndex - itemsMidIndex + initialIndex
    } else {
        listMidIndex + initialIndex - itemsMidIndex
    }.let {
        it - visibleItemsCount / 2
    }
    return Pair(ITEMS_SIZE, targetIndex)
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