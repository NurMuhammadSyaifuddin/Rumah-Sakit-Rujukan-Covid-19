package com.project.profile_hospital

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.project.core.domain.usecase.HospitalUseCase
import com.project.core.utils.PATH_ADMIN
import com.project.core.utils.PATH_REGISTRATION
import com.project.rumahsakitrujukancovid_19.utils.PATH_USER

class ProfileHospitalViewModel(private val useCase: HospitalUseCase): ViewModel() {

    fun getHospital() =
        useCase.getHospitals().asLiveData()

    fun collectionUser(db: FirebaseFirestore) =
        db.collection(PATH_USER)

    fun collectionRegistrationAdmin(db: FirebaseFirestore, email: String) =
        db.collection(PATH_REGISTRATION).document(PATH_ADMIN).collection(email)

    fun collectionRegistrationUserDocument(db: FirebaseFirestore, idUser: String, registrationNumber: String) =
        db.collection(PATH_REGISTRATION).document(PATH_USER).collection(idUser).document(registrationNumber)

    fun collectionRegistrationAdminDocument(db: FirebaseFirestore, email: String, registrationNumber: String) =
        db.collection(PATH_REGISTRATION).document(PATH_ADMIN).collection(email).document(registrationNumber)

    fun storageReference(ref: StorageReference, hospitalName: String) =
        ref.child("images/$hospitalName.jpg")

}