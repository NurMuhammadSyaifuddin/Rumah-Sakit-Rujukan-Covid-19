package com.project.bookmark_user.di

import com.project.bookmark_user.BookmarkHospitalViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val bookmarkModule = module {
    viewModel { BookmarkHospitalViewModel() }
}