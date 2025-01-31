package com.bridge.androidtechnicaltest.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.Single

@Dao
interface PupilDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPupil(pupilList: List<Pupil>)

    @Query("SELECT * FROM Pupils ORDER BY name ASC")
    fun getPupils(): LiveData<List<Pupil>>

    @Query("DELETE FROM Pupils WHERE pupil_id = :id")
    suspend fun deletePupilById(id: Long)

    @Query("DELETE FROM Pupils")
    fun deleteAllPupil()

    @Transaction
    suspend fun clearAndInsertPupil(pupilList: List<Pupil>) {
        deleteAllPupil()
        insertAllPupil(pupilList)
    }
}