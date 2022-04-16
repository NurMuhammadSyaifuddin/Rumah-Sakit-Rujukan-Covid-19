package com.project.hospital_admin.di

import com.project.hospital_admin.ui.home.HomeViewModel
import com.project.hospital_admin.ui.registration.CheckingRegistrationViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val registrationModule = module {
    viewModel { HomeViewModel() }
    viewModel { CheckingRegistrationViewModel() }
}