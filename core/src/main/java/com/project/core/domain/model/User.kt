package com.project.core.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class User(
    var id: String? = null,
    var name: String? = null,
    var email: String? = null,
    var photoUrl: String? = null,
    var status: String? = null
): Parcelable
