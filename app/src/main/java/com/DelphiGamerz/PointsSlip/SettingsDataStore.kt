// SettingsDataStore.kt
package com.DelphiGamerz.PointsSlip // Updated package name

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Create a DataStore instance for preferences
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "points_slip_settings") // Name can remain internal

class SettingsDataStore(private val context: Context) {

    companion object {
        // Key for storing the item counts as a JSON string
        private val ITEM_COUNTS_KEY = stringPreferencesKey("item_counts_list")
    }

    // Flow to observe changes in item counts
    val itemCountsFlow: Flow<List<Int>> = context.dataStore.data
        .map { preferences ->
            val jsonString = preferences[ITEM_COUNTS_KEY]
            if (jsonString != null) {
                try {
                    Json.decodeFromString<List<Int>>(jsonString)
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }

    // Function to save the item counts
    suspend fun saveItemCounts(counts: List<Int>) {
        context.dataStore.edit { preferences ->
            val jsonString = Json.encodeToString(counts)
            preferences[ITEM_COUNTS_KEY] = jsonString
        }
    }

    // Function to load the item counts once
    suspend fun loadInitialItemCounts(defaultSize: Int): List<Int> {
        val preferences = context.dataStore.data.firstOrNull()
        val jsonString = preferences?.get(ITEM_COUNTS_KEY)
        return if (jsonString != null) {
            try {
                Json.decodeFromString<List<Int>>(jsonString)
            } catch (e: Exception) {
                List(defaultSize) { 0 }
            }
        } else {
            List(defaultSize) { 0 }
        }
    }
}
