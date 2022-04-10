package com.project.hospital_admin.di

import com.project.hospital_admin.ui.home.HomeViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val registrationModule = module {
    viewModel { HomeViewModel() }
}