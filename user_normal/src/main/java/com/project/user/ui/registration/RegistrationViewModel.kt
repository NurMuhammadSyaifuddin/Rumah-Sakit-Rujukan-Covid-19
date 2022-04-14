package com.project.user.ui.registration

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.project.core.utils.PATH_ADMIN
import com.project.core.utils.PATH_REGISTRATION
import com.project.rumahsakitrujukancovid_19.utils.PATH_USER

class RegistrationViewModel: ViewModel() {

    fun collectionRegistration(db: FirebaseFirestore, idUser: String, registrationNumber: String) =
        db.collection(PATH_REGISTRATION).document(PATH_USER).collection(idUser).document(registrationNumber)

    fun collectionRegistrationAdmin(db: FirebaseFirestore, emailAdmin: String, registrationNumber: String) =
        db.collection(PATH_REGISTRATION).document(PATH_ADMIN).collection(emailAdmin).document(registrationNumber)

    fun storageReference(ref: StorageReference, uid: String) =
        ref.child("images/$uid.jpg")
}