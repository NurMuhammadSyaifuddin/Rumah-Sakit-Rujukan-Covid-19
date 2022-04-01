package com.project.rumahsakitrujukancovid_19.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.project.rumahsakitrujukancovid_19.utils.USER_KEY
import com.project.rumahsakitrujukancovid_19.utils.USER_NORMAL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LevelUserPreference (private val dataStore: DataStore<Preferences>){

    private val userKey = stringPreferencesKey(USER_KEY)

    suspend fun saveLevelUser(level: String){
        dataStore.edit {
            it[userKey] = level
        }
    }

    fun getLevelUser(): Flow<String> =
        dataStore.data.map {
            it[userKey] ?: USER_NORMAL
        }

//    companion object{
//        @Volatile
//        private var INSTANCE: LevelUserPreference? = null
//
//        fun getInstance(dataStore: DataStore<Preferences>): LevelUserPreference =
//            INSTANCE ?: synchronized(this){
//                INSTANCE ?: LevelUserPreference(dataStore).apply {
//                    INSTANCE = this
//                }
//            }
//    }

}