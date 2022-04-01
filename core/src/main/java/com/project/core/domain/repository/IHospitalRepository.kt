package com.project.core.domain.repository

import com.project.core.data.source.Resource
import com.project.core.domain.model.Hospital
import kotlinx.coroutines.flow.Flow

interface IHospitalRepository {
    fun getHospital(): Flow<Resource<List<Hospital>>>
    fun getSearchHospital(name: String): Flow<List<Hospital>>
}