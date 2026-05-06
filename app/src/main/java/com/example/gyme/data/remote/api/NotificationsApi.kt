package com.example.gyme.data.remote.api

import com.example.gyme.data.remote.dto.*
import retrofit2.http.*

interface NotificationsApi {

    @GET("notifications")
    suspend fun getAll(): List<NotificationDto>

    @GET("notifications")
    suspend fun getByUser(@Query("user_id") userIdFilter: String): List<NotificationDto>

    @POST("notifications")
    @Headers("Prefer: return=representation")
    suspend fun create(@Body body: CreateNotificationRequest): List<NotificationDto>

    @PATCH("notifications")
    @Headers("Prefer: return=representation")
    suspend fun markAsRead(
        @Query("id") idFilter: String,
        @Body body: Map<String, Boolean>
    ): List<NotificationDto>

    @DELETE("notifications")
    suspend fun delete(@Query("id") idFilter: String)
}
