package com.gabrielamaro.spotted.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

val supabase = createSupabaseClient(
    supabaseUrl = "https://pfphdprnjlbbmqcabweu.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InBmcGhkcHJuamxiYm1xY2Fid2V1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjE0OTUyMjUsImV4cCI6MjA3NzA3MTIyNX0.9-mUqjSSVcVH7aQPE12-S0d0ijmjIDBNgDbuBGIyx9E"
) {
    install(Auth)
    install(Postgrest)
}
