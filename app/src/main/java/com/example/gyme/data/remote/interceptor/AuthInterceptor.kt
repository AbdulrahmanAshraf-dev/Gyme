package com.example.gyme.data.remote.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val apiKey: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (apiKey.isBlank()) {
            Log.e("AuthInterceptor", "SUPABASE_API_KEY is blank. " +
                "Add it to local.properties as: SUPABASE_API_KEY=eyJ...")
        }

        val original = chain.request()
        val builder  = original.newBuilder()
            .header("apikey",        apiKey)
            .header("Authorization", "Bearer $apiKey")
            .header("Accept",        "application/json")


        if (original.body != null) {
            builder.header("Content-Type", "application/json")
        }

        return chain.proceed(builder.build())
    }
}
