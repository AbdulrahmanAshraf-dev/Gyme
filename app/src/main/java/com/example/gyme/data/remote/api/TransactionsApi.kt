package com.example.gyme.data.remote.api

import com.example.gyme.data.remote.dto.*
import retrofit2.http.*

interface TransactionsApi {

    @GET("transactions")
    suspend fun getAll(): List<TransactionDto>

    @GET("transactions")
    suspend fun getById(@Query("id") idFilter: String): List<TransactionDto>

    @POST("transactions")
    @Headers("Prefer: return=representation")
    suspend fun create(@Body body: CreateTransactionRequest): List<TransactionDto>

    @PATCH("transactions")
    @Headers("Prefer: return=representation")
    suspend fun update(
        @Query("id") idFilter: String,
        @Body body: UpdateTransactionRequest
    ): List<TransactionDto>

    @DELETE("transactions")
    suspend fun delete(@Query("id") idFilter: String)
}
