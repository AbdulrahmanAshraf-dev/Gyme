package com.example.gyme.util

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
}

suspend fun <T> safeApiCall(call: suspend () -> T): ApiResult<T> {
    return try {
        ApiResult.Success(call())
    } catch (e: Exception) {
        ApiResult.Error(message = e.localizedMessage ?: "Unknown error")
    }
}
