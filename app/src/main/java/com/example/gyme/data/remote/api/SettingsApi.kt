package com.example.gyme.data.remote.api

import com.example.gyme.data.remote.dto.*
import retrofit2.http.*

interface SettingsApi {

    @GET("settings")
    suspend fun get(): List<SettingsDto>

    @POST("settings")
    @Headers("Prefer: return=representation")
    suspend fun create(@Body body: UpsertSettingsRequest): List<SettingsDto>

    @PATCH("settings")
    @Headers("Prefer: return=representation")
    suspend fun update(
        @Query("id") idFilter: String,
        @Body body: UpsertSettingsRequest
    ): List<SettingsDto>
}
