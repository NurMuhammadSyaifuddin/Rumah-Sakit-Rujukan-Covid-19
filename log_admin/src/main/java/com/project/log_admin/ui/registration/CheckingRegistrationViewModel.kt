package com.project.log_admin.ui.registration

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.utils.PATH_ADMIN
import com.project.core.utils.PATH_REGISTRATION
import com.project.rumahsakitrujukancovid_19.utils.PATH_USER

class CheckingRegistrationViewModel: ViewModel() {
    fun collectionRegistration(db: FirebaseFirestore, idUser: String, registrationNumber: String) =
        db.collection(PATH_REGISTRATION).document(PATH_USER).collection(idUser).document(registrationNumber)

    fun collectionRegistrationAdmin(db: FirebaseFirestore, emailAdmin: String, registrationNumber: String) =
        db.collection(PATH_REGISTRATION).document(PATH_ADMIN).collection(emailAdmin).document(registrationNumber)

    fun getCollectionRegistrationAdmin(db: FirebaseFirestore, emailAdmin: String) =
        db.collection(PATH_REGISTRATION).document(PATH_ADMIN).collection(emailAdmin)

    fun collectionUser(db: FirebaseFirestore) =
        db.collection(PATH_USER)
}