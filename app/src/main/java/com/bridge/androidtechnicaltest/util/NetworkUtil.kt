package com.bridge.androidtechnicaltest.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import retrofit2.Response

abstract class NetworkUtil {

    protected suspend fun <T> getResultFromNetwork(networkCall: suspend () -> Response<T>): Resource<T> {
        return try {
            val response = networkCall()
            if (response.isSuccessful) {
                val body = response.body()
                Resource.success(body as T)
            } else {
                Log.d("NetworkUtil", "Error Occured:${response.code()}  ${response.body()}")
                val errorMessage = when {
                   response.message().isNullOrEmpty() -> "Unable to reach server. Please check your connection"
                    else -> response.message()
                }

                Resource.error(errorMessage)
            }
        } catch (e: Exception) {
            Log.d("NetworkUtil", "Error Occured: ${e.message}")
            Resource.error("Unable to reach server. Please check your connection")
        }
    }

}

@SuppressLint("NewApi")
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    connectivityManager ?: return false

    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}