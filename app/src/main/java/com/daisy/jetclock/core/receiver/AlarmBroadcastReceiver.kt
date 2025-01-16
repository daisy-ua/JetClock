package com.daisy.jetclock.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.daisy.jetclock.core.alarm.AlarmAction
import com.daisy.jetclock.core.task.WorkRequestManager
import com.daisy.jetclock.core.task.worker.RESCHEDULE_ALARM_TAG
import com.daisy.jetclock.core.task.worker.RescheduleAlarmWorker
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
    lateinit var workRequestManager: WorkRequestManager

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
                            Intent.ACTION_BOOT_COMPLETED -> {
                                workRequestManager.enqueueWorker<RescheduleAlarmWorker>(
                                    RESCHEDULE_ALARM_TAG
                                )
                            }

                            ACTION_DISMISS -> {
                                actionHandler.dismiss(id)
                            }

                            ACTION_SNOOZE -> {
                                actionHandler.snooze(id)
                            }

                            ACTION_CANCEL -> {
                                actionHandler.cancel(id)
                            }

                            else -> {
                                actionHandler.start(id)
                            }
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
        const val ACTION_DISMISS = "ACTION_DISMISS"
        const val ACTION_SNOOZE = "ACTION_SNOOZE"
        const val ACTION_CANCEL = "ACTION_CANCEL"
    }
}