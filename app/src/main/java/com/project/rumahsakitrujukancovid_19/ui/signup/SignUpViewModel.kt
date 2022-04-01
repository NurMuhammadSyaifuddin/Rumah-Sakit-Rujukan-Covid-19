package com.project.rumahsakitrujukancovid_19.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.data.source.Resource
import com.project.core.domain.model.Hospital
import com.project.core.domain.usecase.HospitalUseCase
import com.project.rumahsakitrujukancovid_19.preference.LevelUserPreference
import com.project.rumahsakitrujukancovid_19.utils.PATH_USER
import kotlinx.coroutines.launch

class SignUpViewModel(private val pref: LevelUserPreference, private val hospitalUseCase: HospitalUseCase) : ViewModel() {

    private val db by lazy { FirebaseDatabase.getInstance().getReference(PATH_HOSPITALS) }

    fun setCollectionUser(db: FirebaseFirestore, uid: String) =
        db.collection(PATH_USER).document(uid)

    fun saveLevelUser(level: String) {
        viewModelScope.launch {
            pref.saveLevelUser(level)
        }
    }

    fun getHospital(): LiveData<Resource<List<Hospital>>> =
        hospitalUseCase.getHospitals().asLiveData()

    fun updateIdHospitalAdmin(id: String) =
        db.child("id_admin").setValue(id)

    companion object{
        private const val PATH_HOSPITALS = "hospitals"
    }
}