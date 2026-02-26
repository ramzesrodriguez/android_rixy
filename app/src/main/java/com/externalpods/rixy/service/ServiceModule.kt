package com.externalpods.rixy.service

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val serviceModule = module {
    // Services
    single { AuthService(get(), get(), get(), get()) }
    single { PaymentService(get()) }
    single { ImageUploadService(get(), get(), androidContext()) }
    single { AnalyticsService(get()) }
}
