package com.project.core.domain.usecase

import com.project.core.data.source.Resource
import com.project.core.domain.model.Hospital
import kotlinx.coroutines.flow.Flow

interface HospitalUseCase {
    fun getHospitals(): Flow<Resource<List<Hospital>>>
    fun getSearchHospial(name: String): Flow<List<Hospital>>
}