package com.bridge.androidtechnicaltest.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bridge.androidtechnicaltest.R
import com.bridge.androidtechnicaltest.network.PupilApi
import com.bridge.androidtechnicaltest.ui.MainActivity
import com.google.gson.JsonObject
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.HttpException

@HiltWorker
class CreateNewPupilWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val pupilApi: PupilApi
) : CoroutineWorker(context, workerParameters) {

    companion object {
        const val NOTIFICATION_ID = 1
    }

    override suspend fun doWork(): Result {
        // Retrieve input data
        val name = inputData.getString("name") ?: return Result.failure()
        val country = inputData.getString("country") ?: return Result.failure()
        val image = inputData.getString("image") ?: ""
        val latitude = inputData.getDouble("latitude", 0.0)
        val longitude = inputData.getDouble("longitude", 0.0)

        val jsonObject = JsonObject()
        jsonObject.apply {
            addProperty("name", name)
            addProperty("country", country)
            addProperty("image", image)
            addProperty("latitude", latitude)
            addProperty("longitude", longitude)
        }

        return try {
            val response = pupilApi.createNewPupil(jsonObject)
            if (response.isSuccessful) {
                val pupilName = response.body()?.name
                sendSuccessNotification(pupilName)
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: HttpException) {
            Result.retry() // Retry on HTTP errors
        } catch (e: Exception) {
            Result.failure() // Fail on other exceptions
        }
    }

    private fun sendSuccessNotification(pupilName: String?) {
        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission not granted
                return
            }
        }

        // Intent to open the app
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(applicationContext, "PUPIL_CREATION_CHANNEL")
            .setSmallIcon(R.drawable.ic_profile_placeholder)
            .setContentTitle("Pupil Created")
            .setContentText("Pupil '$pupilName' has been added successfully.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Pupil Creation"
            val descriptionText = "Notifications for Pupil Creation Status"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("PUPIL_CREATION_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            // Registering channel with the system
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}