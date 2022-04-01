package com.project.core.domain.usecase

import com.project.core.data.source.Resource
import com.project.core.domain.model.Hospital
import com.project.core.domain.repository.IHospitalRepository
import kotlinx.coroutines.flow.Flow

class HospitalInteractor(private val hospitalRepository: IHospitalRepository) : HospitalUseCase {
    override fun getHospitals(): Flow<Resource<List<Hospital>>> = hospitalRepository.getHospital()
    override fun getSearchHospial(name: String): Flow<List<Hospital>> =
        hospitalRepository.getSearchHospital(name)
}