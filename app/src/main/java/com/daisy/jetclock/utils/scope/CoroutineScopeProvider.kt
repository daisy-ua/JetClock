package com.daisy.jetclock.utils.scope

import kotlinx.coroutines.CoroutineScope

interface CoroutineScopeProvider {
    fun getCoroutineScope(): CoroutineScope
}