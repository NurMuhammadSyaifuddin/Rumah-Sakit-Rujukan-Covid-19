package com.project.user.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.project.core.data.source.Resource
import com.project.core.domain.model.Hospital
import com.project.core.domain.usecase.HospitalUseCase
import com.project.rumahsakitrujukancovid_19.utils.PATH_USER

class HomeViewModel(private val hospitalUseCase: HospitalUseCase) : ViewModel() {
    fun getHospital(): LiveData<Resource<List<Hospital>>> =
        hospitalUseCase.getHospitals().asLiveData()

    fun getCollectionUser(db: FirebaseFirestore, id: String) =
        db.collection(PATH_USER).document(id)

    fun getSearchHospital(name: String) = hospitalUseCase.getSearchHospial(name).asLiveData()

    fun storageReference(ref: StorageReference, uid: String) =
        ref.child("images/$uid.jpg")

}