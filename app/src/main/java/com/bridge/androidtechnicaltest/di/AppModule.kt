package com.bridge.androidtechnicaltest.di

import android.content.Context
import androidx.room.Room
import com.bridge.androidtechnicaltest.BuildConfig
import com.bridge.androidtechnicaltest.db.AppDatabase
import com.bridge.androidtechnicaltest.db.PupilDao
import com.bridge.androidtechnicaltest.db.PupilRepository
import com.bridge.androidtechnicaltest.network.PupilApi
import com.bridge.androidtechnicaltest.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun providesPupilApi(): PupilApi {
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

        // Add Logging Interceptor
        if (BuildConfig.DEBUG) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            val bodyLoggingInterceptor = httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(bodyLoggingInterceptor)
        }

        builder.addInterceptor(requestInterceptor)

        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(builder.build())
            .build()
            .create(PupilApi::class.java)
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "TechnicalTestDb")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideDao(appDatabase: AppDatabase): PupilDao {
        return appDatabase.pupilDao()
    }

    @Singleton
    @Provides
    fun providePupilRepository(
        pupilApi: PupilApi,
        pupilDao: PupilDao,
        @ApplicationContext context: Context
    ): PupilRepository {
        return PupilRepository(pupilApi, pupilDao, context)
    }
}