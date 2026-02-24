package com.externalpods.rixy.di

import com.externalpods.rixy.data.local.DataStoreManager
import com.externalpods.rixy.data.local.UserPreferences
import com.externalpods.rixy.data.repository.FavoritesRepository
import com.externalpods.rixy.navigation.AppStateViewModel
import com.externalpods.rixy.feature.user.favorites.FavoritesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Application-level DI module
 */
val appModule = module {
    // DataStore
    single { DataStoreManager(get()) }
    
    // UserPreferences
    single { UserPreferences(get()) }
    
    // Repositories
    single { FavoritesRepository() }
    
    // ViewModels
    viewModel { AppStateViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
}
