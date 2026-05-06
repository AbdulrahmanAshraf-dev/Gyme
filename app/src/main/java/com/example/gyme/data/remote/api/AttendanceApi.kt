package com.example.gyme.data.remote.api

import com.example.gyme.data.remote.dto.*
import retrofit2.http.*

interface AttendanceApi {

    @GET("attendance")
    suspend fun getAll(): List<AttendanceDto>

    @GET("attendance")
    suspend fun getByMember(@Query("member_id") memberIdFilter: String): List<AttendanceDto>

    @POST("attendance")
    @Headers("Prefer: return=representation")
    suspend fun checkIn(@Body body: CreateAttendanceRequest): List<AttendanceDto>

    @DELETE("attendance")
    suspend fun delete(@Query("id") idFilter: String)
}
