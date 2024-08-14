package com.daisy.jetclock.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Data
import com.daisy.jetclock.core.AlarmReceiverActions.ACTION_DISMISS
import com.daisy.jetclock.core.AlarmReceiverActions.ACTION_SNOOZE
import com.daisy.jetclock.core.worker.ALARM_TAG
import com.daisy.jetclock.core.worker.AlarmWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmBroadcastReceiver : BroadcastReceiver() {

//    @Inject
//    lateinit var alarmScheduler: AlarmScheduler

    @Inject lateinit var workRequestManager: WorkRequestManager

    private val broadcastReceiverScope = CoroutineScope(SupervisorJob())

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("daisy-ua", "onReceive: RECEIVED")


        val pendingResult: PendingResult = goAsync()
        broadcastReceiverScope.launch(Dispatchers.Default) {
            try {
                context?.let {

                    intent?.let { intent ->
                        when (intent.action) {
                            ACTION_DISMISS -> {}

                            ACTION_SNOOZE -> {}

                            else -> {
                                val inputData = Data.Builder()
                                    .putString("LABEL", intent.getStringExtra("LABEL"))
                                    .build()

                                workRequestManager.enqueueWorker<AlarmWorker>(
                                    ALARM_TAG,
                                    inputData,
                                )
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
}