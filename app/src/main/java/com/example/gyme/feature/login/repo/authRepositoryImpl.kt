package com.example.gyme.feature.login.repo

import com.example.gyme.feature.members.model.*
import com.example.gyme.feature.staff.model.*
import com.example.gyme.feature.attendance.model.*
import com.example.gyme.feature.finance.model.*
import com.example.gyme.feature.notifications.model.*
import com.example.gyme.feature.more.model.*
import com.example.gyme.feature.home.model.*

import com.example.gyme.core.data.remote.api.supabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import com.example.gyme.util.ApiResult
import com.example.gyme.util.safeApiCall

class AuthRepositoryImpl : AuthRepository {
    override suspend fun signIn(email: String, password: String): ApiResult<User> = safeApiCall {
        val auth = supabaseClient.client.auth
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        val currentUser = auth.currentUserOrNull() ?: throw Exception("User is null")
        User(
            id = currentUser.id,
            email = currentUser.email ?: "",
            name = currentUser.userMetadata?.get("name")?.toString()?.replace("\"", "") ?: "User",
            role = "staff"
        )
    }

    override suspend fun signUp(email: String, password: String): ApiResult<User> = safeApiCall {
        val auth = supabaseClient.client.auth
        auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        val currentUser = auth.currentUserOrNull() ?: throw Exception("User is null")
        User(
            id = currentUser.id,
            email = currentUser.email ?: "",
            name = currentUser.userMetadata?.get("name")?.toString()?.replace("\"", "") ?: "User",
            role = "staff"
        )
    }
}

