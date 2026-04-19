package com.pagshq.murmurbox.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pagshq.murmurbox.data.local.entity.Emotion

@Dao
interface EmotionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(emotions: List<Emotion>)

    @Query("SELECT * FROM emotions")
    suspend fun getAll(): List<Emotion>
}