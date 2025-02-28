package com.daisy.jetclock.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.daisy.jetclock.core.alarm.AlarmAction
import com.daisy.jetclock.core.utils.IntentExtra
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmBroadcastReceiver : BroadcastReceiver() {
    private val broadcastReceiverScope = CoroutineScope(SupervisorJob())

    @Inject
    lateinit var actionHandler: AlarmAction

    override fun onReceive(context: Context?, intent: Intent?) {
        val pendingResult: PendingResult = goAsync()
        broadcastReceiverScope.launch(Dispatchers.Default) {
            try {
                context?.let {
                    intent?.let { intent ->
                        val id = intent.getLongExtra(IntentExtra.ID_EXTRA, -1)

                        when (intent.action) {

                            ACTION_DISMISS -> actionHandler.dismiss(id)

                            ACTION_SNOOZE -> actionHandler.snooze(id)

                            ACTION_CANCEL -> actionHandler.cancel(id)

                            ACTION_START -> actionHandler.start(id)

                            else -> Log.e(
                                "AlarmBroadcastReceiver",
                                "Unknown action ${intent.action}"
                            )
                        }
                    }
                }
            } finally {
                pendingResult.finish()
                broadcastReceiverScope.cancel()
            }
        }
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_DISMISS = "ACTION_DISMISS"
        const val ACTION_SNOOZE = "ACTION_SNOOZE"
        const val ACTION_CANCEL = "ACTION_CANCEL"
    }
}