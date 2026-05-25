package com.texasdex.justthetip

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class RoundingOption {
    UP, DOWN, NEAREST
}

class PreferencesManager(private val context: Context) {
    companion object {
        val DEFAULT_PERCENTAGE = floatPreferencesKey("default_percentage")
        val ROUNDING_OPTION = stringPreferencesKey("rounding_option")
        val ATM_MODE = booleanPreferencesKey("atm_mode")
    }

    val defaultPercentage: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[DEFAULT_PERCENTAGE] ?: 20f
    }

    val roundingOption: Flow<RoundingOption> = context.dataStore.data.map { preferences ->
        val optionName = preferences[ROUNDING_OPTION] ?: RoundingOption.NEAREST.name
        RoundingOption.valueOf(optionName)
    }

    val atmMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ATM_MODE] ?: true
    }

    suspend fun setDefaultPercentage(percentage: Float) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_PERCENTAGE] = percentage
        }
    }

    suspend fun setRoundingOption(option: RoundingOption) {
        context.dataStore.edit { preferences ->
            preferences[ROUNDING_OPTION] = option.name
        }
    }

    suspend fun setAtmMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ATM_MODE] = enabled
        }
    }
}
