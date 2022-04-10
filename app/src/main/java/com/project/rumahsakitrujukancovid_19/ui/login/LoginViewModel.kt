package com.project.rumahsakitrujukancovid_19.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.project.rumahsakitrujukancovid_19.preference.LevelUserPreference
import com.project.rumahsakitrujukancovid_19.utils.PATH_USER
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: LevelUserPreference): ViewModel() {

    fun getCollectionUser(db: FirebaseFirestore) =
        db.collection(PATH_USER)

    fun getLevelUser() = pref.getLevelUser().asLiveData()

    fun saveLevelUser(level: String) {
        viewModelScope.launch {
            pref.saveLevelUser(level)
        }
    }
}