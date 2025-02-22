package com.example.parserapp.data.store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("app_prefs")

class DataStoreManager(private val context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val API_KEY = stringPreferencesKey("api_key")
        val NAME = stringPreferencesKey("name")
        val LAST_CLICK_TIME = stringPreferencesKey("last_click_time")
    }

    suspend fun saveKey(token: String) {
        dataStore.edit { prefs ->
            prefs[API_KEY] = token
        }
    }

    suspend fun saveName(name: String) {
        dataStore.edit { prefs ->
            prefs[NAME] = name
        }
    }

    suspend fun saveLastClickTime(timestamp: Long) {
        dataStore.edit { prefs ->
            prefs[LAST_CLICK_TIME] = timestamp.toString()
        }
    }

    fun getLastClickTime(): Flow<Long> = dataStore.data.map { prefs ->
        prefs[LAST_CLICK_TIME]?.toLong() ?: 0L
    }

    fun getKey(): Flow<String?> = dataStore.data.map { prefs ->
        prefs[API_KEY] ?: ""
    }

    fun getName(): Flow<String?> = dataStore.data.map { prefs ->
        prefs[NAME] ?: ""
    }
}