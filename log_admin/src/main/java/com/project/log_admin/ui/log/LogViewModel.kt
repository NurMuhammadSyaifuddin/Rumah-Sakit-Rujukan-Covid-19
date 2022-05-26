package com.project.log_admin.ui.log

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.utils.PATH_ADMIN
import com.project.core.utils.PATH_REGISTRATION
import com.project.rumahsakitrujukancovid_19.utils.PATH_USER

class LogViewModel: ViewModel() {
    fun collectionUser(db: FirebaseFirestore) =
        db.collection(PATH_USER)

    fun collectionRegistration(db: FirebaseFirestore, emailAdmin: String) =
        db.collection(PATH_REGISTRATION).document(PATH_ADMIN).collection(emailAdmin)
}