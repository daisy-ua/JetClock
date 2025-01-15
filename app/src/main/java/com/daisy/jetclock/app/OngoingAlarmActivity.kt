package com.daisy.jetclock.app

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import com.daisy.jetclock.core.notification.fullscreen.OngoingAlarmFullscreenNotificationHandler.Companion.ACTION_FINISH
import com.daisy.jetclock.presentation.ui.screens.OngoingAlarmScreen
import com.daisy.jetclock.presentation.ui.theme.JetClockTheme
import com.daisy.jetclock.presentation.viewmodel.OngoingAlarmViewModel
import com.daisy.jetclock.utils.turnScreenOnAndKeyguardOff
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OngoingAlarmActivity : ComponentActivity() {

    lateinit var viewModel: OngoingAlarmViewModel

    private var finishReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_FINISH) {
                finish()
            }
        }
    }

    private var unlockReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_USER_PRESENT) {
                viewModel.dismissAlarm()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        turnScreenOnAndKeyguardOff()

        disableBackPress()

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        registerFinishReceiver()
        registerUnlockReceiver()

        setContent {
            JetClockTheme {
                viewModel = hiltViewModel()
                OngoingAlarmScreen()
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerFinishReceiver() {
        val filter = IntentFilter(ACTION_FINISH)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(finishReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(finishReceiver, filter)
        }
    }

    private fun registerUnlockReceiver() {
        val filter = IntentFilter(Intent.ACTION_USER_PRESENT)
        registerReceiver(unlockReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(finishReceiver)
        unregisterReceiver(unlockReceiver)
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        // Do nothing to disable the back button
    }

    private fun disableBackPress() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Do nothing to disable back press
                }
            })
        }
    }
}