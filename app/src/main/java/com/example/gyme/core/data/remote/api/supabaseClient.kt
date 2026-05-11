package com.example.gyme.core.data.remote.api

import com.example.gyme.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.createSupabaseClient

object supabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_API_KEY
    ) {
        install(Auth)
        install(Postgrest)
    }
}