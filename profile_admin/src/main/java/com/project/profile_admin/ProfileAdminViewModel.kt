package com.project.profile_admin

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.project.rumahsakitrujukancovid_19.utils.PATH_USER

class ProfileAdminViewModel: ViewModel() {

    fun collectionUser(db: FirebaseFirestore, uid: String) =
        db.collection(PATH_USER).document(uid)

    fun storageReference(ref: StorageReference, uid: String) =
        ref.child("images/$uid.jpg")

}