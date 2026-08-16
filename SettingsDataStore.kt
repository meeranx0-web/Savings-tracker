package com.savingstracker.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId

val Context.dataStore by preferencesDataStore(name = "settings")

enum class ThemeMode { LIGHT, DARK, SYSTEM }

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val THEME = stringPreferencesKey("theme_mode")
        val START_DATE = longPreferencesKey("challenge_start_epoch_day")
        val DEADLINE = longPreferencesKey("challenge_deadline_epoch_day")
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        when (prefs[Keys.THEME]) {
            "LIGHT" -> ThemeMode.LIGHT
            "DARK" -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME] = mode.name }
    }

    val startDate: Flow<LocalDate> = context.dataStore.data.map { prefs ->
        val epochDay = prefs[Keys.START_DATE]
        if (epochDay != null) LocalDate.ofEpochDay(epochDay) else LocalDate.now()
    }

    suspend fun ensureStartDateSet() {
        context.dataStore.edit { prefs ->
            if (prefs[Keys.START_DATE] == null) {
                prefs[Keys.START_DATE] = LocalDate.now().toEpochDay()
            }
        }
    }

    val deadline: Flow<LocalDate> = context.dataStore.data.map { prefs ->
        val epochDay = prefs[Keys.DEADLINE]
        if (epochDay != null) LocalDate.ofEpochDay(epochDay) else DEFAULT_DEADLINE
    }

    suspend fun resetStartDate() {
        context.dataStore.edit { it[Keys.START_DATE] = LocalDate.now().toEpochDay() }
    }

    companion object {
        val DEFAULT_DEADLINE: LocalDate = LocalDate.of(2026, 12, 25)
        val ZONE = ZoneId.systemDefault()
    }
}
