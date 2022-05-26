package com.project.log_admin.di

import com.project.log_admin.ui.log.LogViewModel
import com.project.log_admin.ui.registration.CheckingRegistrationViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val logModule = module {
    viewModel { LogViewModel() }
    viewModel { CheckingRegistrationViewModel() }
}