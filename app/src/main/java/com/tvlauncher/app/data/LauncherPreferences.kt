package com.tvlauncher.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "launcher_prefs")

class LauncherPreferences(private val context: Context) {

    companion object {
        val GRID_COLUMNS = intPreferencesKey("grid_columns")
        val SHOW_CLOCK = booleanPreferencesKey("show_clock")
        val HIDDEN_APPS = stringPreferencesKey("hidden_apps")
        val APP_ORDER = stringPreferencesKey("app_order")
    }

    private val gson = Gson()

    val gridColumns: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[GRID_COLUMNS] ?: 5
    }

    val showClock: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[SHOW_CLOCK] ?: true
    }

    val hiddenApps: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        val json = prefs[HIDDEN_APPS] ?: "[]"
        val type = object : TypeToken<Set<String>>() {}.type
        gson.fromJson(json, type) ?: emptySet()
    }

    val appOrder: Flow<List<String>> = context.dataStore.data.map { prefs ->
        val json = prefs[APP_ORDER] ?: "[]"
        val type = object : TypeToken<List<String>>() {}.type
        gson.fromJson(json, type) ?: emptyList()
    }

    suspend fun setGridColumns(columns: Int) {
        context.dataStore.edit { prefs ->
            prefs[GRID_COLUMNS] = columns.coerceIn(3, 7)
        }
    }

    suspend fun setShowClock(show: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SHOW_CLOCK] = show
        }
    }

    suspend fun setHiddenApps(packages: Set<String>) {
        context.dataStore.edit { prefs ->
            prefs[HIDDEN_APPS] = gson.toJson(packages)
        }
    }

    suspend fun setAppOrder(packages: List<String>) {
        context.dataStore.edit { prefs ->
            prefs[APP_ORDER] = gson.toJson(packages)
        }
    }
}
