package com.project.core.data.source

import com.google.firebase.database.DataSnapshot

sealed class Resource<T>(val data: T? = null, val snapshot: DataSnapshot? = null, val message: String? = null){
    class Success<T>(data: T, snapshot: DataSnapshot?): Resource<T>(data, snapshot)
    class Loading<T>(data: T? = null): Resource<T>(data)
    class Error<T>(message: String, data: T? = null): Resource<T>(data, null, message)
}
