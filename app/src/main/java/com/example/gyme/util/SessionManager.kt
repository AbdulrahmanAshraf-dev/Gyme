package com.example.gyme.util

import android.content.Context
import com.example.gyme.core.model.User
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object SessionManager {
    private const val PREFS_NAME = "gyme_prefs"
    private const val KEY_USER = "current_user"
    
    var currentUser: User? = null

    fun isLoggedIn(): Boolean = currentUser != null

    fun saveSession(context: Context, user: User) {
        currentUser = user
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userJson = Json.encodeToString(user)
        prefs.edit().putString(KEY_USER, userJson).apply()
    }

    fun loadSession(context: Context): User? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userJson = prefs.getString(KEY_USER, null)
        currentUser = if (userJson != null) {
            try {
                Json.decodeFromString<User>(userJson)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
        return currentUser
    }

    fun clear(context: Context) {
        currentUser = null
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_USER).apply()
    }
}
