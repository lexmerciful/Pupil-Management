package com.bridge.androidtechnicaltest.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Pupil::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pupilDao(): PupilDao
}