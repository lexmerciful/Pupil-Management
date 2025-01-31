package com.bridge.androidtechnicaltest.di

import android.content.Context
import androidx.room.Room
import com.bridge.androidtechnicaltest.db.AppDatabase
import com.bridge.androidtechnicaltest.network.PupilApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val API_TIMEOUT: Long = 30
private const val BASE_URL = "https://androidtechnicaltestapi-test.bridgeinternationalacademies.com/"

object PupilAPIFactory {
    fun retrofitPupil(): PupilApi {
        val builder = OkHttpClient.Builder()
        builder.readTimeout(API_TIMEOUT, TimeUnit.SECONDS)
        builder.writeTimeout(API_TIMEOUT, TimeUnit.SECONDS)
        builder.connectTimeout(API_TIMEOUT, TimeUnit.SECONDS)

        val requestId = "dda7feeb-20af-415e-887e-afc43f245624"
        val userAgent = "Bridge Android Tech Test"
        val requestInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                    .addHeader("X-Request-ID", requestId)
                    .addHeader("User-Agent", userAgent)
                    .build()
            chain.proceed(newRequest)
        }
        builder.addInterceptor(requestInterceptor)

        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(builder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(PupilApi::class.java)
    }
}

object DatabaseFactory {

    fun getDBInstance(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "TechnicalTestDb")
                    .fallbackToDestructiveMigration()
                    .build()
}