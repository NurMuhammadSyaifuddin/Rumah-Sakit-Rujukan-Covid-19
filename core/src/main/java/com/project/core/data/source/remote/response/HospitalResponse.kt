package com.project.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class HospitalResponse(
    @SerializedName("id")
    var id: String? = "",

    @SerializedName("name")
    var name: String? = "",

    @SerializedName("address")
    var address: String? = "",

    @SerializedName("province")
    var province: String? = "",

    @SerializedName("region")
    var region: String? = "",

    @SerializedName("phone")
    var phone: String? = "",

    @SerializedName("image_url")
    var imageUrl: String? = "",

    @SerializedName("website_url")
    var websiteUrl: String? = "",

    @SerializedName("latitude")
    var latitude: Double? = 0.0,

    @SerializedName("longitude")
    var longitude: Double? = 0.0,

    @SerializedName("email_admin")
    var emailAdmin: String? = "",

    @SerializedName("id_admin")
    var idAdmin: String? = ""
)