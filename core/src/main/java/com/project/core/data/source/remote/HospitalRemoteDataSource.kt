package com.project.core.data.source.remote

import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.core.data.source.remote.network.ApiResponse
import com.project.core.data.source.remote.response.HospitalResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
class HospitalRemoteDataSource {

    private val hospitalsDatabase by lazy {
        FirebaseDatabase.getInstance().getReference(PATH_HOSPITAL)
    }

    suspend fun getHospitals() =
        callbackFlow {
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        val json = Gson().toJson(snapshot.value)
                        val type = object : TypeToken<List<HospitalResponse>>() {}.type
                        val hospitals = Gson().fromJson<List<HospitalResponse>>(json, type)

                        trySendBlocking(ApiResponse.Success(hospitals, snapshot))

                    } else {
                        trySendBlocking(ApiResponse.Empty)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    trySendBlocking(ApiResponse.Error(error.message))
                }

            }

            hospitalsDatabase.addValueEventListener(valueEventListener)

            awaitClose { hospitalsDatabase.removeEventListener(valueEventListener) }
        }

    companion object {
        private const val PATH_HOSPITAL = "hospitals"
    }
}