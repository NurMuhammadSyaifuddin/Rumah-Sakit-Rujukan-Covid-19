package com.project.bookmark_user

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.project.rumahsakitrujukancovid_19.utils.PATH_FAVORITE
import com.project.rumahsakitrujukancovid_19.utils.PATH_HOSPITAL

class BookmarkHospitalViewModel: ViewModel() {

    fun getCollectionBookmarkHospital(db: FirebaseFirestore, uid: String) =
        db.collection(PATH_HOSPITAL).document(PATH_FAVORITE)
            .collection(uid)

}