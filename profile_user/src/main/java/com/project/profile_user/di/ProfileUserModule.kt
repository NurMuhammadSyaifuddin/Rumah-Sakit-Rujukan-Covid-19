package com.project.profile_user.di

import com.project.profile_user.ProfileUserViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val profileUserModule = module {
    viewModel { ProfileUserViewModel() }
}