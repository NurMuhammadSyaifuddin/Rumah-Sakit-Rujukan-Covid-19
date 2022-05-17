package com.project.rumahsakitrujukancovid_19.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.project.core.domain.usecase.HospitalInteractor
import com.project.core.domain.usecase.HospitalUseCase
import com.project.rumahsakitrujukancovid_19.preference.LevelUserPreference
import com.project.rumahsakitrujukancovid_19.ui.admin.HospitalAdminLocationViewModel
import com.project.rumahsakitrujukancovid_19.ui.login.LoginViewModel
import com.project.rumahsakitrujukancovid_19.ui.signup.SignUpViewModel
import com.project.rumahsakitrujukancovid_19.ui.splashscreen.SplashScreenViewModel
import com.project.rumahsakitrujukancovid_19.utils.USER_NORMAL
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<HospitalUseCase> { HospitalInteractor(get()) }
}

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { SignUpViewModel(get(), get()) }
    viewModel { HospitalAdminLocationViewModel(get(), get()) }
    viewModel { SplashScreenViewModel(get()) }
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_NORMAL)

val preferenceModule = module {
    factory { get<Context>().dataStore }
    single { LevelUserPreference(get()) }
}