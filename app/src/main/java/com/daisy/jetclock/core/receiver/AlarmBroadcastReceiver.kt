package com.daisy.jetclock.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.daisy.jetclock.core.IntentExtra
import com.daisy.jetclock.core.manager.WorkRequestManager
import com.daisy.jetclock.core.service.AlarmService
import com.daisy.jetclock.core.worker.RESCHEDULE_ALARM_TAG
import com.daisy.jetclock.core.worker.RescheduleAlarmWorker
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
    lateinit var workRequestManager: WorkRequestManager

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
                            Intent.ACTION_BOOT_COMPLETED -> {
                                workRequestManager.enqueueWorker<RescheduleAlarmWorker>(
                                    RESCHEDULE_ALARM_TAG
                                )
                            }

                            ACTION_DISMISS -> {
                                serviceIntent.action = AlarmService.ACTION_DISMISS
                            }

                            ACTION_SNOOZE -> {
                                serviceIntent.action = AlarmService.ACTION_SNOOZE
                            }

                            ACTION_CANCEL -> {
                                serviceIntent.action = AlarmService.ACTION_DISABLE
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
        const val ACTION_CANCEL = "ACTION_CANCEL"
    }
}