package com.externalpods.rixy.service

import com.externalpods.rixy.navigation.AppStateViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val serviceModule = module {
    // App State ViewModel
    viewModel { AppStateViewModel(get()) }
    
    // Services
    single { AuthService(get(), get(), get()) }
    single { PaymentService(get()) }
    single { ImageUploadService(get(), get(), androidContext()) }
    single { AnalyticsService(get()) }
}
