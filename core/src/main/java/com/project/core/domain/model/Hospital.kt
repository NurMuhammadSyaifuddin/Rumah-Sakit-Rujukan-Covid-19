package com.project.core.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class Hospital(
    var id: String? = null,
    var name: String? = null,
    var address: String? = null,
    var province: String? = null,
    var region: String? = null,
    var phone: String? = null,
    var imageUrl: String? = null,
    var websiteUrl: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var isFavorite: Boolean = false,
    var emailAdmin: String? = null,
    var idAdmin: String? = null
): Parcelable
