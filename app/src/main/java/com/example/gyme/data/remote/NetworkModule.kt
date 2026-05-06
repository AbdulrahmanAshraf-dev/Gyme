package com.example.gyme.data.remote

import com.example.gyme.BuildConfig
import com.example.gyme.data.remote.interceptor.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object NetworkModule {

    private val baseUrl: String
        get() = BuildConfig.SUPABASE_URL.trimEnd('/') + "/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(BuildConfig.SUPABASE_API_KEY))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val dbRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("${baseUrl}rest/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val authRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("${baseUrl}auth/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> create(service: Class<T>): T = dbRetrofit.create(service)

    fun <T> createAuth(service: Class<T>): T = authRetrofit.create(service)
}
