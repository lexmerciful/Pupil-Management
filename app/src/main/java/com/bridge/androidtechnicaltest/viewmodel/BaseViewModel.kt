package com.bridge.androidtechnicaltest.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.bridge.androidtechnicaltest.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseViewModel : ViewModel() {
    fun <T> resultLiveData(
        scope: CoroutineScope = viewModelScope,
        networkCall: suspend () -> Resource<T>
    ): LiveData<Resource<T>> {
        return liveData(scope.coroutineContext) {
            emit(Resource.loading())

            withContext(Dispatchers.IO){
                emit(networkCall.invoke())
            }
        }
    }
}