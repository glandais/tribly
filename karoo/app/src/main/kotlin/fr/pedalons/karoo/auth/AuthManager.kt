package fr.pedalons.karoo.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import fr.pedalons.karoo.api.TokenResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.authDataStore: DataStore<Preferences> by
    preferencesDataStore(name = "pedalons_auth")

/**
 * Manages authentication tokens using DataStore. Tokens are stored locally and refreshed
 * automatically when needed.
 */
class AuthManager(private val context: Context) {

    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val EXPIRES_AT = longPreferencesKey("expires_at")
    }

    /** Stores tokens from a successful authentication. */
    suspend fun saveTokens(tokenResponse: TokenResponse) {
        val expiresAt = System.currentTimeMillis() + (tokenResponse.expiresIn * 1000L)
        context.authDataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = tokenResponse.accessToken
            tokenResponse.refreshToken?.let { prefs[Keys.REFRESH_TOKEN] = it }
            prefs[Keys.EXPIRES_AT] = expiresAt
        }
    }

    /** Updates only the access token (used after refresh). */
    suspend fun updateAccessToken(accessToken: String, expiresIn: Int) {
        val expiresAt = System.currentTimeMillis() + (expiresIn * 1000L)
        context.authDataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = accessToken
            prefs[Keys.EXPIRES_AT] = expiresAt
        }
    }

    /** Clears all stored tokens. */
    suspend fun clearTokens() {
        context.authDataStore.edit { prefs ->
            prefs.remove(Keys.ACCESS_TOKEN)
            prefs.remove(Keys.REFRESH_TOKEN)
            prefs.remove(Keys.EXPIRES_AT)
        }
    }

    /** Gets the current access token if not expired. */
    suspend fun getValidAccessToken(): String? {
        val prefs = context.authDataStore.data.first()
        val accessToken = prefs[Keys.ACCESS_TOKEN] ?: return null
        val expiresAt = prefs[Keys.EXPIRES_AT] ?: return null

        // Token is valid if it has more than 5 minutes remaining
        return if (System.currentTimeMillis() < expiresAt - 5 * 60 * 1000) {
            accessToken
        } else {
            null
        }
    }

    /** Gets the refresh token if available. */
    suspend fun getRefreshToken(): String? {
        return context.authDataStore.data.first()[Keys.REFRESH_TOKEN]
    }

    /** Flow indicating whether the user is authenticated. */
    val isAuthenticated: Flow<Boolean> =
        context.authDataStore.data.map { prefs ->
            val hasAccessToken = prefs[Keys.ACCESS_TOKEN] != null
            val hasRefreshToken = prefs[Keys.REFRESH_TOKEN] != null
            hasAccessToken && hasRefreshToken
        }

    /** Checks if token needs refresh (expired or expiring soon). */
    suspend fun needsRefresh(): Boolean {
        val prefs = context.authDataStore.data.first()
        val expiresAt = prefs[Keys.EXPIRES_AT] ?: return true
        // Refresh if less than 5 minutes remaining
        return System.currentTimeMillis() >= expiresAt - 5 * 60 * 1000
    }
}
