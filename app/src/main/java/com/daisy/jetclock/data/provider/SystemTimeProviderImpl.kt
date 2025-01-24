package com.daisy.jetclock.data.provider

import android.content.Context
import android.text.format.DateFormat
import com.daisy.jetclock.domain.model.TimeFormat
import com.daisy.jetclock.domain.provider.SystemTimeProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SystemTimeProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : SystemTimeProvider {

    override fun getSystemTimeFormat(): TimeFormat {
        val is24Hour = DateFormat.is24HourFormat(context)
        return if (is24Hour) TimeFormat.Hour24Format else TimeFormat.Hour12Format
    }
}