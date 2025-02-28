package com.daisy.jetclock.core.notification.foreground

import android.app.Notification
import android.content.Context
import com.daisy.jetclock.core.notification.AlarmNotificationManager
import com.daisy.jetclock.core.notification.AlarmNotificationType
import com.daisy.jetclock.domain.model.Alarm
import com.daisy.jetclock.domain.usecase.GetTimeFormatUseCase
import com.daisy.jetclock.presentation.utils.formatter.TimeFormatter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ForegroundServiceNotificationImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: AlarmNotificationManager,
    private val getTimeFormatUseCase: GetTimeFormatUseCase,
) : ForegroundServiceNotification<Alarm> {

    override suspend fun getNotification(data: Alarm): Notification {
        val timeFormat = getTimeFormatUseCase().first()
        val timeString =
            data.triggerTime?.let { TimeFormatter.formatTimeWithMeridiem(context, it, timeFormat) }
        val notificationType = AlarmNotificationType.Ongoing(data.id, data.label, timeString)

        return notificationManager.createNotification(notificationType)
    }
}