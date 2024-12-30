package com.daisy.jetclock.core.receiver

import android.content.Context
import android.content.Intent
import com.daisy.jetclock.core.utils.IntentExtra
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaPlaybackHandler @Inject constructor(
    @ApplicationContext private val context: Context,
) : ServiceHandler<Long> {

    override fun stop(key: Long) {
        val intent = Intent(context, AlarmBroadcastReceiver::class.java).apply {
            action = AlarmBroadcastReceiver.ACTION_CANCEL
            putExtra(IntentExtra.ID_EXTRA, key)
        }

        context.sendBroadcast(intent)
    }
}