package com.project.core.di

import androidx.room.Room
import com.project.core.data.source.local.HospitalDatabase
import com.project.core.data.source.local.HospitalLocalDataSource
import com.project.core.data.source.remote.HospitalRemoteDataSource
import com.project.core.data.source.repository.HospitalRepository
import com.project.core.domain.repository.IHospitalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    factory { get<HospitalDatabase>().hospitalDao() }
    single {
        Room.databaseBuilder(
            androidContext(),
            HospitalDatabase::class.java,
            "hospital.db"
        ).fallbackToDestructiveMigration()
            .build()
    }
}

@ExperimentalCoroutinesApi
val repositoryModule = module {
    single { HospitalLocalDataSource(get()) }
    single { HospitalRemoteDataSource() }
    single<IHospitalRepository> {
        HospitalRepository(
            get(),
            get(),
            androidContext()
        )
    }
}