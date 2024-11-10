package com.tron.ehguardian.data.repositories

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val IS_PUSH_NOTIFICATIONS_ENABLED = booleanPreferencesKey("is_push_notifications_enabled")
    }

    suspend fun savePushNotificationsPreferences(isPushNotificationsEnabled: Boolean) {
        dataStore.edit{ preferences ->
            preferences[IS_PUSH_NOTIFICATIONS_ENABLED] = isPushNotificationsEnabled


        }


    }
    val isPushNotificationsEnabled: Flow<Boolean> = dataStore.data.catch {
        if(it is IOException) {
            Log.e("UserPreferencesRepo", "Error reading preferences.", it)
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        preferences[IS_PUSH_NOTIFICATIONS_ENABLED] ?: false
    }
}