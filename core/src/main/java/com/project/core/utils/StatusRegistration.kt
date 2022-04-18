package com.project.core.utils

import android.content.Context
import com.project.core.R
import java.text.SimpleDateFormat
import java.util.*

fun Context.wait() = getString(R.string.wait)
fun Context.accepted() = getString(R.string.accepted)
fun Context.rejected() = getString(R.string.rejected)

const val PATH_REGISTRATION = "registration"
const val PATH_ADMIN = "admin"

fun getRandomIdString(): String =
    List(16){
        (('a'..'z') + ('A'..'Z') + ('0'..'9')).random()
    }.joinToString("")

fun getRandomIdNumber(): String =
    List(16){
        ('0'..'9').random()
    }.joinToString("")

fun getRandomStringSingle(): String =
    List(16){
        (('a'..'z') + ('A'..'Z') + ('0'..'9')).random()
    }.joinToString("")

fun getRandomIntSingle(): Int =
    List(8){
        ('0'..'9').random()
    }.joinToString("").toInt()

fun getCurrentTime(): String =
    SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())

fun getCurrentTimeToGetQueue(): String =
    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

fun isCurrentTimeTheSame(currentTime: String): Boolean =
    currentTime == getCurrentTimeToGetQueue()