package com.bridge.androidtechnicaltest.network

import com.bridge.androidtechnicaltest.db.Pupil
import com.bridge.androidtechnicaltest.db.PupilList
import com.google.gson.JsonObject
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PupilApi {
    @GET("pupils")
    suspend fun getPupils(
        @Query("page") page: Long = 1
    ): Response<PupilList>

    @POST("pupils")
    suspend fun createNewPupil(
        @Body jsonObject: JsonObject
    ): Response<Pupil>

    @PUT("pupils/{pupilId}")
    suspend fun editPupil(
        @Path("pupilId") pupilId: Int,
        @Body jsonObject: JsonObject
    ): Response<Pupil>

    @DELETE("pupils/{pupilId}")
    suspend fun deletePupil(
        @Path("pupilId") pupilId: Int
    ): Response<ResponseBody>

}