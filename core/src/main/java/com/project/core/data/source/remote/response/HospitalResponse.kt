package com.project.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class HospitalResponse(
    @SerializedName("id")
    val id: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("address")
    val address: String?,

    @SerializedName("province")
    val province: String?,

    @SerializedName("region")
    val region: String?,

    @SerializedName("phone")
    val phone: String?,

    @SerializedName("image_url")
    val imageUrl: String?,

    @SerializedName("website_url")
    val websiteUrl: String?,

    @SerializedName("latitude")
    val latitude: Double?,

    @SerializedName("longitude")
    val longitude: Double?,

    @SerializedName("email_admin")
    val emailAdmin: String?,

    @SerializedName("id_admin")
    val idAdmin: String?
)