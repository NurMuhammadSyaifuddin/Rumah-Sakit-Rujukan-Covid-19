package com.project.core.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class Registration(
    var id: String? = null,
    var idUser: String? = null,
    var photoUrl: String? = null,
    var registrationNumber: String? = null,
    var registrationDate: String? = null,
    var acceptDate: String? = null,
    var name: String? = null,
    var hospitalName: String? = null,
    var emailAdmin: String? = null,
    var imageUrl: String? = null,
    var note: String? = null,
    var statusRegistration: String? = null,
    var queue: Int? = 0,
    var typeActivities: String? = null,
    @field:JvmField
    var isShowNotif: Boolean = false,
    var referredTo: String? = null
) : Parcelable
