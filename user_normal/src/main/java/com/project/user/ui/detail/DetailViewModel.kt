package com.project.user.ui.detail

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.project.core.utils.PATH_ADMIN
import com.project.rumahsakitrujukancovid_19.utils.PATH_FAVORITE
import com.project.rumahsakitrujukancovid_19.utils.PATH_HOSPITAL
import com.project.rumahsakitrujukancovid_19.utils.PATH_USER
import com.project.core.utils.PATH_REGISTRATION

class DetailViewModel: ViewModel() {

    fun setCollectionHospitalFavorite(db: FirebaseFirestore, uid: String, id: String) =
        db.collection(PATH_HOSPITAL).document(PATH_FAVORITE)
            .collection(uid).document(id)

    fun collectionRegistration(db: FirebaseFirestore, idUser: String, registrationNumber: String) =
        db.collection(PATH_REGISTRATION).document(PATH_USER).collection(idUser).document(registrationNumber)

    fun getCollectionRegistration(db: FirebaseFirestore, idUser: String) =
        db.collection(PATH_REGISTRATION).document(PATH_USER).collection(idUser)

    fun collectionRegistrationAdmin(db: FirebaseFirestore, hospitalName: String, registrationNumber: String) =
        db.collection(PATH_REGISTRATION).document(PATH_ADMIN).collection(hospitalName).document(registrationNumber)

    fun collectionUser(db: FirebaseFirestore, uid: String) =
        db.collection(PATH_USER).document(uid)

    fun storageReference(ref: StorageReference, uid: String) =
        ref.child("images/$uid.jpg")
}