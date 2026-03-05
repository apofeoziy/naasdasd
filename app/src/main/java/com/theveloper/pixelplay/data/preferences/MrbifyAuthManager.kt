package com.theveloper.pixelplay.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.theveloper.pixelplay.data.network.mrbify.MrbifyLoginRequest
import com.theveloper.pixelplay.data.network.mrbify.MrbifyRegisterRequest
import com.theveloper.pixelplay.data.network.mrbify.MrbifyUser
import com.theveloper.pixelplay.data.repository.MrbifyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MrbifyAuthManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val repository: MrbifyRepository
) {
    // We use the same keys declared in UserPreferencesRepository to keep it simple,
    // or redefine them here for encapsulation. We will redefine them here referencing the same string names.
    private object Keys {
        val USER_ID = stringPreferencesKey("mrbify_user_id")
        val USERNAME = stringPreferencesKey("mrbify_username")
        val TOKEN = stringPreferencesKey("mrbify_token")
        val AVATAR_URL = stringPreferencesKey("mrbify_avatar_url")
    }

    val currentUserFlow: Flow<MrbifyUser?> = dataStore.data.map { prefs ->
        val id = prefs[Keys.USER_ID] ?: return@map null
        val username = prefs[Keys.USERNAME] ?: ""
        val token = prefs[Keys.TOKEN]
        val avatarUrl = prefs[Keys.AVATAR_URL]
        MrbifyUser(id, username, token, avatarUrl)
    }

    val isLoggedInFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.USER_ID] != null
    }

    suspend fun login(request: MrbifyLoginRequest): Result<MrbifyUser> {
        val result = repository.login(request)
        if (result.isSuccess) {
            val user = result.getOrThrow()
            saveSession(user)
        }
        return result
    }

    suspend fun register(request: MrbifyRegisterRequest): Result<MrbifyUser> {
        val result = repository.register(request)
        if (result.isSuccess) {
            val user = result.getOrThrow()
            saveSession(user)
        }
        return result
    }

    suspend fun logout() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.USER_ID)
            prefs.remove(Keys.USERNAME)
            prefs.remove(Keys.TOKEN)
            prefs.remove(Keys.AVATAR_URL)
        }
    }

    private suspend fun saveSession(user: MrbifyUser) {
        dataStore.edit { prefs ->
            prefs[Keys.USER_ID] = user.id
            prefs[Keys.USERNAME] = user.username
            if (user.token != null) {
                prefs[Keys.TOKEN] = user.token
            } else {
                prefs.remove(Keys.TOKEN)
            }
            if (user.avatarUrl != null) {
                prefs[Keys.AVATAR_URL] = user.avatarUrl
            } else {
                prefs.remove(Keys.AVATAR_URL)
            }
        }
    }
}
