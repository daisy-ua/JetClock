package com.daisy.jetclock.domain.repository

import com.daisy.jetclock.domain.model.TimeFormat

interface TimeFormatStorage {

    suspend fun getTimeFormat(): TimeFormat

    suspend fun setTimeFormat(timeFormat: TimeFormat)
}