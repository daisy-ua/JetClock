package com.daisy.jetclock.core.utils

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
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

fun <T> getParcelableExtra(intent: Intent?, key: String, classType: Class<out T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        intent?.getParcelableExtra(key, classType)
    } else {
        intent?.getParcelableExtra(key)
    }
}