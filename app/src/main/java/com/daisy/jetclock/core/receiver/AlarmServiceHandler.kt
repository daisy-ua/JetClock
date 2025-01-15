package com.daisy.jetclock.core.receiver

import android.content.Context
import android.content.Intent
import com.daisy.jetclock.core.utils.IntentExtra
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmServiceHandler @Inject constructor(
    @ApplicationContext private val context: Context,
) : ServiceHandler<Long> {

    override fun start(key: Long, serviceAction: String) {
        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            action = serviceAction
            putExtra(IntentExtra.ID_EXTRA, key)
        }

        context.sendBroadcast(intent)
    }
}