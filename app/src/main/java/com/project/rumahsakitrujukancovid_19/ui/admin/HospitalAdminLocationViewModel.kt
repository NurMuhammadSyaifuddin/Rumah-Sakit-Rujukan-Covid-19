package com.project.rumahsakitrujukancovid_19.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.domain.usecase.HospitalUseCase
import com.project.rumahsakitrujukancovid_19.preference.LevelUserPreference
import com.project.rumahsakitrujukancovid_19.utils.PATH_USER
import kotlinx.coroutines.launch

class HospitalAdminLocationViewModel(private val pref: LevelUserPreference, private val hospitalUseCase: HospitalUseCase) : ViewModel() {
    fun getHospitalName() =
        hospitalUseCase.getHospitals().asLiveData()

    fun setCollectionUser(db: FirebaseFirestore, uid: String) =
        db.collection(PATH_USER).document(uid)

    fun saveLevelUser(level: String) {
        viewModelScope.launch {
            pref.saveLevelUser(level)
        }
    }

}
