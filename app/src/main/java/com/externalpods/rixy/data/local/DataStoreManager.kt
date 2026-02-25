package com.externalpods.rixy.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.externalpods.rixy.core.model.AppMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "rixy_preferences")

class DataStoreManager(private val context: Context) {

    // City selection
    val selectedCityId: Flow<String?> = context.dataStore.data.map { it[CITY_ID] }
    val selectedCitySlug: Flow<String?> = context.dataStore.data.map { it[CITY_SLUG] }
    val selectedCityName: Flow<String?> = context.dataStore.data.map { it[CITY_NAME] }
    val selectedCityState: Flow<String?> = context.dataStore.data.map { it[CITY_STATE] }
    val selectedCityCountry: Flow<String?> = context.dataStore.data.map { it[CITY_COUNTRY] }

    suspend fun saveSelectedCity(id: String, slug: String, name: String, state: String?, country: String?) {
        context.dataStore.edit { prefs ->
            prefs[CITY_ID] = id
            prefs[CITY_SLUG] = slug
            prefs[CITY_NAME] = name
            state?.let { prefs[CITY_STATE] = it }
            country?.let { prefs[CITY_COUNTRY] = it }
        }
    }

    suspend fun clearSelectedCity() {
        context.dataStore.edit { prefs ->
            prefs.remove(CITY_ID)
            prefs.remove(CITY_SLUG)
            prefs.remove(CITY_NAME)
        }
    }

    // App mode
    val currentMode: Flow<AppMode> = context.dataStore.data.map { prefs ->
        prefs[APP_MODE]?.let { AppMode.valueOf(it) } ?: AppMode.USER
    }

    suspend fun saveMode(mode: AppMode) {
        context.dataStore.edit { it[APP_MODE] = mode.name }
    }

    // Auth state
    val isAuthenticated: Flow<Boolean> = context.dataStore.data.map { it[IS_AUTHENTICATED] ?: false }
    val currentUserId: Flow<String?> = context.dataStore.data.map { it[USER_ID] }
    val currentUserEmail: Flow<String?> = context.dataStore.data.map { it[USER_EMAIL] }
    val favoritesJson: Flow<String?> = context.dataStore.data.map { it[FAVORITES_JSON] }

    suspend fun saveAuthState(userId: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[IS_AUTHENTICATED] = true
            prefs[USER_ID] = userId
            prefs[USER_EMAIL] = email
        }
    }

    suspend fun clearAuthState() {
        context.dataStore.edit { prefs ->
            prefs[IS_AUTHENTICATED] = false
            prefs.remove(USER_ID)
            prefs.remove(USER_EMAIL)
        }
    }

    suspend fun saveFavoritesJson(value: String) {
        context.dataStore.edit { prefs ->
            prefs[FAVORITES_JSON] = value
        }
    }

    suspend fun clearFavorites() {
        context.dataStore.edit { prefs ->
            prefs.remove(FAVORITES_JSON)
        }
    }

    companion object {
        private val CITY_ID = stringPreferencesKey("selected_city_id")
        private val CITY_SLUG = stringPreferencesKey("selected_city_slug")
        private val CITY_NAME = stringPreferencesKey("selected_city_name")
        private val CITY_STATE = stringPreferencesKey("selected_city_state")
        private val CITY_COUNTRY = stringPreferencesKey("selected_city_country")
        private val APP_MODE = stringPreferencesKey("app_mode")
        private val IS_AUTHENTICATED = booleanPreferencesKey("is_authenticated")
        private val USER_ID = stringPreferencesKey("current_user_id")
        private val USER_EMAIL = stringPreferencesKey("current_user_email")
        private val FAVORITES_JSON = stringPreferencesKey("favorites_json")
    }
}
