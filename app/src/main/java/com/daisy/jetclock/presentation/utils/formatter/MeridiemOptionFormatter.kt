package com.daisy.jetclock.presentation.utils.formatter

import android.content.Context
import com.daisy.jetclock.R
import com.daisy.jetclock.constants.MeridiemOption


fun MeridiemOption.getLocalizedString(context: Context): String {
    return when (this) {
        MeridiemOption.AM -> context.getString(R.string.meridiem_am)
        MeridiemOption.PM -> context.getString(R.string.meridiem_pm)
    }
}