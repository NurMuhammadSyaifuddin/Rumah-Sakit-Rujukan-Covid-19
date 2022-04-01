package com.project.rumahsakitrujukancovid_19

import android.app.Application
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.project.core.di.databaseModule
import com.project.core.di.repositoryModule
import com.project.rumahsakitrujukancovid_19.di.preferenceModule
import com.project.rumahsakitrujukancovid_19.di.useCaseModule
import com.project.rumahsakitrujukancovid_19.di.viewModelModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

@ExperimentalCoroutinesApi
class BaseApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@BaseApplication)
            modules(
                viewModelModule,
                databaseModule,
                repositoryModule,
                useCaseModule,
                preferenceModule
            )
        }
        if (BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
//            Firebase.database.useEmulator("10.0.2.2", 9000)
//            Firebase.auth.useEmulator("10.0.2.2", 9099)
//            Firebase.storage.useEmulator("10.0.2.2", 9199)
        }
    }
}