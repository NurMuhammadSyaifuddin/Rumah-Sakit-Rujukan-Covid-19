package com.project.core.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.core.data.source.local.entity.HospitalEntitiy
import kotlinx.coroutines.flow.Flow

@Dao
interface HospitalDao {

    @Query("SELECT * FROM hospitals")
    fun getHospitals(): Flow<List<HospitalEntitiy>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHospital(hospitals: List<HospitalEntitiy>)

    @Query("SELECT * FROM hospitals WHERE name LIKE '%' || :name || '%'")
    fun getSearchHospital(name: String): Flow<List<HospitalEntitiy>>
}