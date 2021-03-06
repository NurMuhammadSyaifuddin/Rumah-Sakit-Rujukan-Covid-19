package com.project.user.ui.detail

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.project.rumahsakitrujukancovid_19.utils.PATH_BOOKMARK
import com.project.rumahsakitrujukancovid_19.utils.PATH_HOSPITAL
import com.project.rumahsakitrujukancovid_19.utils.PATH_USER
import com.project.core.utils.PATH_REGISTRATION

class DetailViewModel: ViewModel() {

    fun setCollectionHospitalFavorite(db: FirebaseFirestore, uid: String, id: String) =
        db.collection(PATH_HOSPITAL).document(PATH_BOOKMARK)
            .collection(uid).document(id)

    fun getCollectionRegistration(db: FirebaseFirestore, idUser: String) =
        db.collection(PATH_REGISTRATION).document(PATH_USER).collection(idUser)

    fun collectionUser(db: FirebaseFirestore, uid: String) =
        db.collection(PATH_USER).document(uid)
}