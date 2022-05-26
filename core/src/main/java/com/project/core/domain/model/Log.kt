package com.project.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Log(
    val date: String,
    val waiting: Int,
    val rejected: Int,
    val accepted: Int
): Parcelable
