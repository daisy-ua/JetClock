package com.daisy.jetclock.presentation.ui.component.animation.listitemplacement

import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun calculateDiff(
    detectMoves: Boolean = true,
    diffCb: DiffUtil.Callback,
): DiffUtil.DiffResult {
    return withContext(Dispatchers.Unconfined) {
        DiffUtil.calculateDiff(diffCb, detectMoves)
    }
}