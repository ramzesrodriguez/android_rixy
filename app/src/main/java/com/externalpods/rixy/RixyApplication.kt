package com.externalpods.rixy

import android.app.Application
import com.externalpods.rixy.core.network.networkModule
import com.externalpods.rixy.data.dataModule
import com.externalpods.rixy.domain.domainModule
import com.externalpods.rixy.service.serviceModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class RixyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@RixyApplication)
            modules(
                networkModule,
                dataModule,
                domainModule,
                serviceModule
            )
        }
    }
}
