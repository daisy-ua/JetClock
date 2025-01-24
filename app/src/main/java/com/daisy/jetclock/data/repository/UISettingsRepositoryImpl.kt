package com.daisy.jetclock.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.daisy.jetclock.data.repository.utils.tryIt
import com.daisy.jetclock.domain.model.TimeFormat
import com.daisy.jetclock.domain.pref.TimeFormatPref
import com.daisy.jetclock.domain.provider.SystemTimeProvider
import com.daisy.jetclock.domain.repository.UISettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UISettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val systemTimeProvider: SystemTimeProvider,
) : UISettingsRepository {

    override val timeFormat: Flow<TimeFormat>
        get() = dataStore.data
            .map { preferences ->
                val pref = preferences[PreferencesKeys.TIME_FORMAT]?.let {
                    runCatching { TimeFormatPref.valueOf(it) }.getOrNull()
                } ?: TimeFormatPref.SystemDefault

                resolveTimeFormat(pref)
            }

    override suspend fun setTimeFormat(timeFormat: TimeFormatPref) {
        tryIt {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.TIME_FORMAT] = timeFormat.name
            }
        }
    }

    private fun resolveTimeFormat(pref: TimeFormatPref): TimeFormat {
        return when (pref) {
            TimeFormatPref.Hour12Format -> TimeFormat.Hour12Format
            TimeFormatPref.Hour24Format -> TimeFormat.Hour24Format
            TimeFormatPref.SystemDefault -> systemTimeProvider.getSystemTimeFormat()
        }
    }

    private object PreferencesKeys {
        val TIME_FORMAT = stringPreferencesKey("time_format")
    }
}