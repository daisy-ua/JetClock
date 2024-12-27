package com.daisy.jetclock.core.notification

import com.daisy.jetclock.core.base.NotificationType

sealed class AlarmNotificationType : NotificationType {

    data class Ongoing(
        val id: Long,
        val label: String? = null,
        val time: String? = null,
    ) : AlarmNotificationType() {

        override val notificationId: Int
            get() = NotificationConfig.ALARM_ONGOING_NOTIFICATION_ID + id.toInt()
    }

    data class Missed(
        val id: Long,
        val label: String? = null,
        val time: String? = null,
    ) : AlarmNotificationType() {

        override val notificationId: Int
            get() = NotificationConfig.ALARM_MISSED_NOTIFICATION_ID + id.toInt()
    }

    data class Snoozed(
        val id: Long,
        val label: String? = null,
        val time: String? = null,
    ) : AlarmNotificationType() {

        override val notificationId: Int
            get() = NotificationConfig.ALARM_SNOOZED_NOTIFICATION_ID + id.toInt()
    }
}