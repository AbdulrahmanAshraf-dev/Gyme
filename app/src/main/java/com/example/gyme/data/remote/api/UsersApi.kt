package com.example.gyme.data.remote.api

import com.example.gyme.data.remote.dto.*
import retrofit2.http.*

interface UsersApi {

    @GET("users")
    suspend fun getAll(): List<UserDto>

    @GET("users")
    suspend fun getById(@Query("id") idFilter: String): List<UserDto>

    @GET("users")
    suspend fun getByEmail(@Query("email") emailFilter: String): List<UserDto>

    @POST("users")
    @Headers("Prefer: return=representation")
    suspend fun create(@Body body: CreateUserRequest): List<UserDto>

    @PATCH("users")
    @Headers("Prefer: return=representation")
    suspend fun update(
        @Query("id") idFilter: String,
        @Body body: UpdateUserRequest
    ): List<UserDto>

    @DELETE("users")
    suspend fun delete(@Query("id") idFilter: String)
}
