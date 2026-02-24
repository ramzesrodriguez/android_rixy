package com.externalpods.rixy.core.network

object ApiConfig {
    // Use 10.0.2.2 for Android emulator to reach host machine localhost
    const val BASE_URL = "http://10.0.2.2:3000/api/v1/"
    // const val BASE_URL = "https://api.rixy.app/api/v1/" // Production

    const val SUPABASE_URL = "https://xawldrmuwlfvjucqlnyw.supabase.co"
    const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inhhd2xkcm11d2xmdmp1Y3Fsbnl3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzgyNTkwOTQsImV4cCI6MjA1MzgzNTA5NH0.xJoEH1OZ7jmb-CoIWKKJNjpHX1VG-jN3RGJh3T9CRbA"

    const val STRIPE_PUBLISHABLE_KEY = "pk_test_placeholder"
}
