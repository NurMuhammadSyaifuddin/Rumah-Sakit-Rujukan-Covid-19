package com.project.profile_hospital.di

import com.project.profile_hospital.ProfileHospitalViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val profileHospitalModule = module {
    viewModel { ProfileHospitalViewModel(get()) }
}