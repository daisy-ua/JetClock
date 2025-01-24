package com.daisy.jetclock.domain.provider

import com.daisy.jetclock.domain.model.TimeFormat

interface SystemTimeProvider {

    fun getSystemTimeFormat(): TimeFormat
}