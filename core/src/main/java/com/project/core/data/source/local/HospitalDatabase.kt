package com.project.core.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.project.core.data.source.local.entity.HospitalEntitiy
import com.project.core.data.source.local.room.HospitalDao

@Database(entities = [HospitalEntitiy::class], version = 1, exportSchema = false )
abstract class HospitalDatabase: RoomDatabase() {
    abstract fun hospitalDao(): HospitalDao
}