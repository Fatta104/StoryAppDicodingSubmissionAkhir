package com.fattanaufal.storyapp.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")


class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val SESSION_KEY = stringPreferencesKey("user_data")

    fun getSessionUser(): Flow<UserSession> {
        return dataStore.data.map { preferences ->
            Gson().fromJson(preferences[SESSION_KEY] ?: "", UserSession::class.java)
        }
    }

    suspend fun saveSessionUser(session: UserSession) {
        dataStore.edit { preferences ->
            preferences[SESSION_KEY] = Gson().toJson(session)
        }
    }

    suspend fun removeSessionUser(){
        dataStore.edit {preferences ->
            preferences.remove(SESSION_KEY)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}