package com.project.activity_user.di

import com.project.activity_user.ActivityUserViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val activityUserModule = module {
    viewModel { ActivityUserViewModel() }
}