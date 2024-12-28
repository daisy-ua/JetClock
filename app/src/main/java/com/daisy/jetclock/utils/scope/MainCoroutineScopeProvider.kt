package com.daisy.jetclock.utils.scope

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

class MainCoroutineScopeProvider @Inject constructor() : CoroutineScopeProvider {
    override fun getCoroutineScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.Main + SupervisorJob())
    }
}