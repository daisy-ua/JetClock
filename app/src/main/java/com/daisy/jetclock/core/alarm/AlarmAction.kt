package com.daisy.jetclock.core.alarm

interface AlarmAction {

    suspend fun start(alarmId: Long)

    suspend fun snooze(id: Long)

    suspend fun dismiss(id: Long)

    suspend fun cancel(id: Long)
}