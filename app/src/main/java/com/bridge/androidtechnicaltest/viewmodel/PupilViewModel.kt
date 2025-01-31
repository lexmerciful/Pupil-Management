package com.bridge.androidtechnicaltest.viewmodel

import com.bridge.androidtechnicaltest.db.Pupil
import com.bridge.androidtechnicaltest.db.PupilRepository
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PupilViewModel @Inject constructor(
    private val pupilRepository: PupilRepository
): BaseViewModel() {

    fun getAllPupil() = pupilRepository.getOrFetchPupils()

    fun createNewPupilOld(jsonObject: JsonObject) = resultLiveData {
        pupilRepository.createNewPupilOld(jsonObject)
    }

    fun editPupil(pupilId: Int, jsonObject: JsonObject) = resultLiveData {
        pupilRepository.editPupil(pupilId, jsonObject)
    }

    fun deletePupil(pupilId: Int) = resultLiveData {
        pupilRepository.deletePupil(pupilId)
    }

    fun createNewPupil(pupil: Pupil): UUID {
        return pupilRepository.createNewPupil(pupil)
    }
}