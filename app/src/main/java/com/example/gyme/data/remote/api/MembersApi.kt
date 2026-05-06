package com.example.gyme.data.remote.api

import com.example.gyme.data.remote.dto.*
import retrofit2.http.*

interface MembersApi {

    @GET("members")
    suspend fun getAll(): List<MemberDto>

    @GET("members")
    suspend fun getById(@Query("id") idFilter: String): List<MemberDto>

    @POST("members")
    @Headers("Prefer: return=representation")
    suspend fun create(@Body body: CreateMemberRequest): List<MemberDto>

    @PATCH("members")
    @Headers("Prefer: return=representation")
    suspend fun update(
        @Query("id") idFilter: String,
        @Body body: UpdateMemberRequest
    ): List<MemberDto>

    @DELETE("members")
    suspend fun delete(@Query("id") idFilter: String)
}
