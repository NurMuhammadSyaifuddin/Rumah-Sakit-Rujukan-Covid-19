package com.project.core.data.source.remote.network

import com.google.firebase.database.DataSnapshot

sealed class ApiResponse<out R>{
    data class Success<out T>(val data: T, val snapshot: DataSnapshot): ApiResponse<T>()
    data class Error(val errorMessage: String): ApiResponse<Nothing>()
    object Empty: ApiResponse<Nothing>()
}
