package com.example.gyme.data.remote.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

data class AuthRequest(
    @SerializedName("email")      val email: String,
    @SerializedName("password")   val password: String,
    @SerializedName("grant_type") val grantType: String = "password"
)

data class SignUpRequest(
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String
)

data class AuthResponse(
    @SerializedName("access_token")  val accessToken: String?  = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
    @SerializedName("token_type")    val tokenType: String?    = null,
    @SerializedName("expires_in")    val expiresIn: Int?       = null,
    @SerializedName("user")          val user: AuthUserDto?    = null
)

data class AuthUserDto(
    @SerializedName("id")             val id: String?    = null,
    @SerializedName("email")          val email: String? = null,
    @SerializedName("role")           val role: String?  = null,
    @SerializedName("created_at")     val createdAt: String? = null,
    @SerializedName("user_metadata")  val userMetadata: Map<String, Any?>? = null
)

interface SupabaseAuthApi {

    @POST("token")
    suspend fun signIn(
        @Query("grant_type") grantType: String = "password",
        @Body body: AuthRequest
    ): AuthResponse

    @POST("signup")
    suspend fun signUp(
        @Body body: SignUpRequest
    ): AuthResponse
}
