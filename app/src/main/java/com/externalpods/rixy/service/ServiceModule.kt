package com.externalpods.rixy.service

import com.externalpods.rixy.navigation.AppState
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val serviceModule = module {
    // Global App State (singleton)
    single { AppState(get()) }
    
    // Services
    single { AuthService(get(), get(), get()) }
    single { PaymentService(get()) }
    single { ImageUploadService(get(), get(), androidContext()) }
    single { AnalyticsService(get()) }
}
