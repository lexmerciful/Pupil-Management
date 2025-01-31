package com.bridge.androidtechnicaltest.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bridge.androidtechnicaltest.network.PupilApi
import com.bridge.androidtechnicaltest.util.NetworkUtil
import com.bridge.androidtechnicaltest.util.Resource
import com.bridge.androidtechnicaltest.util.performGetOperation
import com.bridge.androidtechnicaltest.worker.CreateNewPupilWorker
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import java.util.UUID
import javax.inject.Inject

interface IPupilRepository {
    fun getOrFetchPupils() : LiveData<Resource<List<Pupil>>>

    suspend fun createNewPupilOld(jsonObject: JsonObject) : Resource<Pupil>

    suspend fun editPupil(pupilId: Int, jsonObject: JsonObject) : Resource<Pupil>

    suspend fun deletePupil(pupilId: Int) : Resource<ResponseBody>
}

class PupilRepository @Inject constructor(
    private val pupilApi: PupilApi,
    private val pupilDao: PupilDao,
    private val context: Context
) : IPupilRepository, NetworkUtil() {

    override fun getOrFetchPupils() = performGetOperation(
        databaseQuery = { pupilDao.getPupils() },
        networkCall = { getPupils() },
        saveCallResult = { pupilList ->
            pupilDao.clearAndInsertPupil(pupilList.items)
        }
    )

    private suspend fun getPupils() = getResultFromNetwork {
        pupilApi.getPupils(1)
    }

    override suspend fun createNewPupilOld(jsonObject: JsonObject) = getResultFromNetwork {
        pupilApi.createNewPupil(jsonObject)
    }

    override suspend fun editPupil(pupilId: Int, jsonObject: JsonObject) = getResultFromNetwork {
        pupilApi.editPupil(pupilId, jsonObject)
    }

    override suspend fun deletePupil(pupilId: Int) = getResultFromNetwork {
        pupilApi.deletePupil(pupilId)
    }

    fun createNewPupil(pupil: Pupil): UUID {
        // Create Pupil input data
        val inputData = Data.Builder()
            .putString("name", pupil.name)
            .putString("country", pupil.country)
            .putString("image", pupil.image)
            .putDouble("latitude", pupil.latitude)
            .putDouble("longitude", pupil.longitude)
            .build()

        // Setting network constraints
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<CreateNewPupilWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)

        // Returning WorkRequest ID to track status
        return workRequest.id
    }

}