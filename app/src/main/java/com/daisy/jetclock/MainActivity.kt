package com.daisy.jetclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.daisy.jetclock.ui.screens.AlarmScreen
import com.daisy.jetclock.ui.theme.JetClockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetClockTheme {
                AlarmScreen()
            }
        }
    }
}