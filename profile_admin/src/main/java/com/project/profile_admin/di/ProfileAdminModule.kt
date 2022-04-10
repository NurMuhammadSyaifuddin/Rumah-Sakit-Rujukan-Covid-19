package com.project.profile_admin.di

import com.project.profile_admin.ProfileAdminViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val profileAdminModule = module {
    viewModel { ProfileAdminViewModel() }
}