package com.daisy.jetclock.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.daisy.jetclock.core.AlarmService
import com.daisy.jetclock.core.receiver.AlarmReceiverActions.ACTION_DISMISS
import com.daisy.jetclock.core.receiver.AlarmReceiverActions.ACTION_SNOOZE
import com.daisy.jetclock.repositories.AlarmRepository
import com.daisy.jetclock.utils.MediaPlayerManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var mediaPlayerManager: MediaPlayerManager

    @Inject
    lateinit var alarmRepository: AlarmRepository


    private val broadcastReceiverScope = CoroutineScope(SupervisorJob())

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("daisy-ua", "onReceive: RECEIVED")

        val pendingResult: PendingResult = goAsync()
        broadcastReceiverScope.launch(Dispatchers.Default) {
            try {
                context?.let {

                    intent?.let { intent ->
                        when (intent.action) {
                            ACTION_DISMISS -> {
                                val alarmId = intent.getLongExtra("ID", -1)
                                if (alarmId != -1L) {
                                    alarmRepository.getAlarmById(alarmId).first()?.also { alarm ->
                                        if (alarm.repeatDays.isEmpty()) {
                                            alarm.isEnabled = false
                                            alarmRepository.insertAlarm(alarm)
                                        }
                                    }
                                } else {
                                    Log.d("daisy-ua", "onReceive: wrong id $alarmId")
                                }

                                context.stopService(Intent(context, AlarmService::class.java))
                            }

                            ACTION_SNOOZE -> {
                                val alarmId = intent.getLongExtra("ID", -1)
                                if (alarmId != -1L) {
                                    alarmRepository.getAlarmById(alarmId).first()?.also { alarm ->
                                        if (alarm.repeatDays.isEmpty()) {
                                            alarm.isEnabled = false
                                            alarmRepository.insertAlarm(alarm)
                                        }
                                    }
                                } else {
                                    Log.d("daisy-ua", "onReceive: wrong id $alarmId")
                                }

                                context.stopService(Intent(context, AlarmService::class.java))
                            }

                            else -> {
                                val serviceIntent =
                                    Intent(context, AlarmService::class.java).apply {
                                        putExtra("LABEL", intent.getStringExtra("LABEL"))
                                        putExtra("TIME", intent.getStringExtra("TIME"))
                                        putExtra("ID", intent.getLongExtra("ID", -1))
                                    }

                                context.startService(serviceIntent)
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