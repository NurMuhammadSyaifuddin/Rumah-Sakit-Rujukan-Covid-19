package com.project.core.data.source.repository

import android.content.Context
import com.project.core.data.source.NetworkBoundService
import com.project.core.data.source.Resource
import com.project.core.data.source.local.HospitalLocalDataSource
import com.project.core.data.source.remote.HospitalRemoteDataSource
import com.project.core.data.source.remote.network.ApiResponse
import com.project.core.data.source.remote.response.HospitalResponse
import com.project.core.domain.model.Hospital
import com.project.core.domain.repository.IHospitalRepository
import com.project.core.utils.DataMapper
import com.project.core.utils.isInternetConnected
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ExperimentalCoroutinesApi
class HospitalRepository(
    private val remote: HospitalRemoteDataSource,
    private val local: HospitalLocalDataSource,
    private val context: Context
) : IHospitalRepository {
    override fun getHospital(): Flow<Resource<List<Hospital>>> =
        object : NetworkBoundService<List<Hospital>, List<HospitalResponse>>() {
            override suspend fun saveCallResult(data: List<HospitalResponse>) =
                local.insertHospitals(DataMapper.mapResponseToEntitiesHospital(data))

            override fun shouldFetch(data: List<Hospital>): Boolean =
                isInternetConnected(context)


            override fun loadFromDb(): Flow<List<Hospital>> =
                local.getHospitals().map {
                    DataMapper.mapEntitiesToDomainHospitals(it)
                }

            override suspend fun createCall(): Flow<ApiResponse<List<HospitalResponse>>> =
                remote.getHospitals()

        }.asFlow()

    override fun getSearchHospital(name: String): Flow<List<Hospital>> =
        local.getSearchHospital(name).map {
            DataMapper.mapEntitiesToDomainHospitals(it)
        }
}