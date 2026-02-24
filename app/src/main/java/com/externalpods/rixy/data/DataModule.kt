package com.externalpods.rixy.data

import com.externalpods.rixy.data.local.DataStoreManager
import com.externalpods.rixy.data.local.TokenManager
import com.externalpods.rixy.data.repository.*
import com.externalpods.rixy.navigation.AppState
import com.externalpods.rixy.service.PaymentHandler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { TokenManager(androidContext()) }
    single { DataStoreManager(androidContext()) }

    single<CityRepository> { CityRepositoryImpl(get()) }
    single<ListingRepository> { ListingRepositoryImpl(get()) }
    single<BusinessRepository> { BusinessRepositoryImpl(get()) }
    single<OwnerRepository> { OwnerRepositoryImpl(get()) }
    single<AdminRepository> { AdminRepositoryImpl(get()) }
    
    // Payment handling
    single { PaymentHandler(get()) }
    
    // App State
    single { AppState(get()) }
}
