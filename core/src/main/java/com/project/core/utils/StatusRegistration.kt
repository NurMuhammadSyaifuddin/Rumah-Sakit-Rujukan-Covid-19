package com.project.core.utils

import java.text.SimpleDateFormat
import java.util.*

const val WAIT = "Menunggu Konfirmasi Admin"
const val ACCEPT = "Diterima"
const val REJECT = "Ditolak"

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

fun getCurrentTime(): String =
    SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())