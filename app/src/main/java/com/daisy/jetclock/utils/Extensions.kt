package com.daisy.jetclock.utils

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.daisy.jetclock.constants.ConfigConstants


fun Class<out BroadcastReceiver>?.setIntentAction(
    actionName: String,
    requestCode: Int,
    extras: Map<String, Any> = emptyMap(),
    context: Context,
): PendingIntent {
    val broadcastIntent =
        Intent(context, this).apply {
            action = actionName
            extras.forEach { (key, value) ->
                when (value) {
                    is String -> putExtra(key, value)
                    is Int -> putExtra(key, value)
                    is Long -> putExtra(key, value)
                    is Boolean -> putExtra(key, value)
                    else -> throw IllegalArgumentException("Unsupported extra type")
                }
            }
        }
    return PendingIntent.getBroadcast(
        context,
        requestCode,
        broadcastIntent,
        ConfigConstants.PENDING_INTENT_FLAGS,
    )
}