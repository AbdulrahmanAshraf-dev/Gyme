package com.example.gyme.util

import com.example.gyme.domain.model.User

object SessionManager {
    var currentUser: User? = null

    fun isLoggedIn(): Boolean = currentUser != null

    fun clear() {
        currentUser = null
    }
}
