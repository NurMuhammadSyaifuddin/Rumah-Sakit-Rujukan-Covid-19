package com.project.activity_user

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.utils.PATH_REGISTRATION
import com.project.rumahsakitrujukancovid_19.utils.PATH_USER

class ActivityUserViewModel : ViewModel() {

    fun collectionRegistration(db: FirebaseFirestore, idUser: String) =
        db.collection(PATH_REGISTRATION).document(PATH_USER).collection(idUser)

}