package com.bridge.androidtechnicaltest.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers

fun <T, A> performGetOperation(
    databaseQuery: () -> LiveData<T>,
    networkCall: suspend () -> Resource<A>,
    saveCallResult: suspend (A) -> Unit
): LiveData<Resource<T>> = liveData(Dispatchers.IO) {
    emit(Resource.loading())

    val databaseSource = databaseQuery.invoke().map {
        Resource.success(it)
    }
    emitSource(databaseSource)

    val responseStatus = networkCall.invoke()
    if (responseStatus.status == Resource.Status.SUCCESS) {
        saveCallResult(responseStatus.data!!)
    } else if (responseStatus.status == Resource.Status.ERROR) {
        emit(Resource.error(responseStatus.message!!))
        emitSource(databaseSource)
    }
}