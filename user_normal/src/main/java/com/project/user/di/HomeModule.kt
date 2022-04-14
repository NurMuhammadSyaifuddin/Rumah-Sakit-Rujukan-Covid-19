package com.project.user.di

import com.project.user.ui.detail.DetailViewModel
import com.project.user.ui.home.HomeViewModel
import com.project.user.ui.registration.RegistrationViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { DetailViewModel() }
    viewModel { RegistrationViewModel() }
}