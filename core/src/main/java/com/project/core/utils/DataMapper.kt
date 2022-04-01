package com.project.core.utils

import com.project.core.data.source.local.entity.HospitalEntitiy
import com.project.core.data.source.remote.response.HospitalResponse
import com.project.core.domain.model.Hospital

object DataMapper {
    fun mapResponseToEntitiesHospital(input: List<HospitalResponse>): List<HospitalEntitiy> =
        input.map{
            HospitalEntitiy(
                it.id as String,
                it.name,
                it.address,
                it.province,
                it.region,
                it.phone,
                it.imageUrl,
                it.websiteUrl,
                it.latitude,
                it.longitude,
                false,
                it.emailAdmin,
                it.idAdmin
            )
        }

    fun mapEntitiesToDomainHospitals(input: List<HospitalEntitiy>): List<Hospital> =
        input.map {
            Hospital(
                it.id,
                it.name,
                it.address,
                it.province,
                it.region,
                it.phone,
                it.imageUrl,
                it.websiteUrl,
                it.latitude,
                it.longitude,
                it.isFavorite,
                it.emailAdmin,
                it.idAdmin
            )
        }
}