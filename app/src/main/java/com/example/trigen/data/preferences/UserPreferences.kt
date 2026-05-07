package com.example.trigen.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class UserPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    val darkModeFlow: Flow<Boolean?> = context.dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY]
    }

    suspend fun setDarkMode(isDark: Boolean?) {
        context.dataStore.edit { preferences ->
            if (isDark == null) {
                preferences.remove(DARK_MODE_KEY)
            } else {
                preferences[DARK_MODE_KEY] = isDark
            }
        }
    }
}