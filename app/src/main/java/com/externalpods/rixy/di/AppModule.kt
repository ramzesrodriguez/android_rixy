package com.externalpods.rixy.di

import com.externalpods.rixy.navigation.AppState
import org.koin.dsl.module

/**
 * Application-level DI module
 */
val appModule = module {
    // AppState singleton
    single { AppState(get()) }
}
