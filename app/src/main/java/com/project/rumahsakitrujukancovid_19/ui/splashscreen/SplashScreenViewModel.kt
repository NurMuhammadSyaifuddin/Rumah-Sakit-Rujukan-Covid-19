package com.project.rumahsakitrujukancovid_19.ui.splashscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.project.rumahsakitrujukancovid_19.preference.LevelUserPreference

class SplashScreenViewModel(private val pref: LevelUserPreference): ViewModel() {

    fun getLevelUser() = pref.getLevelUser().asLiveData()

}