package com.project.profile_user

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.project.core.utils.PATH_ADMIN
import com.project.core.utils.PATH_REGISTRATION
import com.project.rumahsakitrujukancovid_19.utils.PATH_USER

class ProfileUserViewModel: ViewModel() {

    fun collectionUser(db: FirebaseFirestore, uid: String) =
        db.collection(PATH_USER).document(uid)

    fun storageReference(ref: StorageReference, uid: String) =
        ref.child("images/$uid.jpg")

    fun collectionRegistrationUser(db: FirebaseFirestore, idUser: String) =
        db.collection(PATH_REGISTRATION).document(PATH_USER).collection(idUser)

    fun collectionRegistrationUserDocument(db: FirebaseFirestore, idUser: String, registrationNumber: String) =
        db.collection(PATH_REGISTRATION).document(PATH_USER).collection(idUser).document(registrationNumber)

    fun collectionRegistrationAdminDocument(db: FirebaseFirestore, email: String, registrationNumber: String) =
        db.collection(PATH_REGISTRATION).document(PATH_ADMIN).collection(email).document(registrationNumber)

}