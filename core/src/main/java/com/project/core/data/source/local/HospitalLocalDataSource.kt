package com.project.core.data.source.local

import com.project.core.data.source.local.entity.HospitalEntitiy
import com.project.core.data.source.local.room.HospitalDao
import kotlinx.coroutines.flow.Flow

class HospitalLocalDataSource(private val hospitalDao: HospitalDao) {
    fun getHospitals(): Flow<List<HospitalEntitiy>> = hospitalDao.getHospitals()
    suspend fun insertHospitals(hospitals: List<HospitalEntitiy>) = hospitalDao.insertHospital(hospitals)
    fun getSearchHospital(name: String) = hospitalDao.getSearchHospital(name)
}