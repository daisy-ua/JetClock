package com.daisy.jetclock.ui.component.animation.listitemplacement

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback


@Composable
fun <T> updateAnimatedItemsState(
    newList: List<T>,
    keyExtractor: (T) -> Any,
): State<List<AnimatedItem<T>>> {
    val state = remember { mutableStateOf(emptyList<AnimatedItem<T>>()) }

    LaunchedEffect(newList) {
        if (state.value == newList) {
            return@LaunchedEffect
        }
        val oldList = state.value.toList()

        val diffCb = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldList.size
            override fun getNewListSize(): Int = newList.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                oldList[oldItemPosition].item == newList[newItemPosition]

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                oldList[oldItemPosition].item == newList[newItemPosition]

        }
        val diffResult = calculateDiff(false, diffCb)
        val compositeList = oldList.toMutableList()

        diffResult.dispatchUpdatesTo(object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {
                for (i in 0 until count) {
                    val newItem = newList[position + i]
                    val newItemKey = keyExtractor(newItem)

                    val existingItemIndex = compositeList.indexOfFirst {
                        keyExtractor(it.item) == newItemKey
                    }

                    if (existingItemIndex == -1) {
                        val animatedItem = AnimatedItem(
                            visibility = MutableTransitionState(true),
                            item = newItem
                        )
                        animatedItem.visibility.targetState = true
                        compositeList.add(position + i, animatedItem)
                    } else {
                        compositeList[existingItemIndex] = AnimatedItem(
                            visibility = MutableTransitionState(true),
                            item = newItem
                        )
                    }
                }
            }

            override fun onRemoved(position: Int, count: Int) {
                for (i in 0 until count) {
                    compositeList[position + i].visibility.targetState = false
                }
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                // not detecting moves.
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {
                // irrelevant with compose.
            }
        })

        if (state.value != compositeList) {
            state.value = compositeList
        }

        val initialAnimation = androidx.compose.animation.core.Animatable(1.0f)
        initialAnimation.animateTo(0f)

        state.value = state.value.filter { it.visibility.targetState }
    }

    return state
}