package com.example.gyme.data.remote

import com.example.gyme.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.okhttp.OkHttp
import java.util.concurrent.TimeUnit


object supabaseSdk {

    val supabase = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_API_KEY
    ) {
        httpEngine = OkHttp.create {
            config {
                connectTimeout(30, TimeUnit.SECONDS)
                readTimeout(30, TimeUnit.SECONDS)
                writeTimeout(30, TimeUnit.SECONDS)
            }
        }
        install(Auth)
        install(Postgrest)
    }
}
