package com.daisy.jetclock.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.daisy.jetclock.constants.ConfigConstants
import com.daisy.jetclock.domain.model.TimeFormat
import com.daisy.jetclock.domain.repository.UISettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UISettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : UISettingsRepository {

    private object PreferencesKeys {
        val TIME_FORMAT = stringPreferencesKey("time_format")
    }

    override val timeFormat: Flow<TimeFormat>
        get() = dataStore.data
            .map { preferences ->
                val pref = preferences[PreferencesKeys.TIME_FORMAT]
                pref?.let { TimeFormat.valueOf(pref) } ?: ConfigConstants.DEFAULT_TIME_FORMAT
            }


    override suspend fun setTimeFormat(timeFormat: TimeFormat) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TIME_FORMAT] = timeFormat.name
        }
    }
}