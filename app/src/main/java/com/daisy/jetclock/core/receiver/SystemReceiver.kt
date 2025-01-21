package com.daisy.jetclock.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.daisy.jetclock.core.task.WorkRequestManager
import com.daisy.jetclock.core.task.worker.RESCHEDULE_ALARM_TAG
import com.daisy.jetclock.core.task.worker.RescheduleAlarmWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SystemReceiver : BroadcastReceiver() {
    private val broadcastReceiverScope = CoroutineScope(SupervisorJob())

    @Inject
    lateinit var workRequestManager: WorkRequestManager

    override fun onReceive(context: Context?, intent: Intent?) {
        val pendingResult: PendingResult = goAsync()
        broadcastReceiverScope.launch(Dispatchers.Default) {
            try {
                context?.let {
                    intent?.let { intent ->
                        when (intent.action) {
                            Intent.ACTION_BOOT_COMPLETED -> handleReschedule()

                            Intent.ACTION_LOCALE_CHANGED -> handleLocaleChanged()

                            Intent.ACTION_TIME_CHANGED,
                            Intent.ACTION_TIMEZONE_CHANGED -> handleReschedule()

                            else -> {
                                Log.d("SystemReceiver", "time is set")
                                handleReschedule()
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

    private fun handleReschedule() {
        workRequestManager.enqueueWorker<RescheduleAlarmWorker>(
            RESCHEDULE_ALARM_TAG
        )
    }

    private fun handleLocaleChanged() {
        Log.d("SystemReceiver", "onReceive: locale changed")
    }
}