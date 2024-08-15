package com.daisy.jetclock.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.daisy.jetclock.core.AlarmService
import com.daisy.jetclock.core.IntentExtra
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlarmBroadcastReceiver : BroadcastReceiver() {
    private val broadcastReceiverScope = CoroutineScope(SupervisorJob())

    override fun onReceive(context: Context?, intent: Intent?) {
        val pendingResult: PendingResult = goAsync()
        broadcastReceiverScope.launch(Dispatchers.Default) {
            try {
                context?.let {
                    intent?.let { intent ->
                        val serviceIntent =
                            Intent(context, AlarmService::class.java).apply {
                                putExtra(
                                    IntentExtra.ID_EXTRA,
                                    intent.getLongExtra(IntentExtra.ID_EXTRA, -1)
                                )
                            }

                        when (intent.action) {
                            ACTION_DISMISS -> {
                                serviceIntent.action = AlarmService.ACTION_DISMISS
                            }

                            ACTION_SNOOZE -> {
                                serviceIntent.action = AlarmService.ACTION_SNOOZE
                            }

                            else -> {
                                serviceIntent.apply {
                                    putExtra(
                                        IntentExtra.SNOOZED_TIMESTAMP_EXTRA,
                                        intent.getStringExtra(IntentExtra.SNOOZED_TIMESTAMP_EXTRA)
                                    )
                                    action = AlarmService.ACTION_START
                                }
                            }
                        }

                        context.startService(serviceIntent)
                    }
                }
            } finally {
                pendingResult.finish()
                broadcastReceiverScope.cancel()
            }
        }
    }

    companion object {
        const val ACTION_DISMISS = "ACTION_DISMISS"
        const val ACTION_SNOOZE = "ACTION_SNOOZE"
    }
}