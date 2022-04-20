package com.project.hospital_admin.ui.home

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.utils.PATH_ADMIN
import com.project.core.utils.PATH_REGISTRATION
import com.project.rumahsakitrujukancovid_19.utils.PATH_USER

class HomeViewModel: ViewModel() {

    fun collectionUser(db: FirebaseFirestore) =
        db.collection(PATH_USER)

    fun collectionRegistration(db: FirebaseFirestore, emailAdmin: String) =
        db.collection(PATH_REGISTRATION).document(PATH_ADMIN).collection(emailAdmin)
}