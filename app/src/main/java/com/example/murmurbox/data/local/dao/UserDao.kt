package com.example.murmurbox.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.murmurbox.data.local.entity.User

@Dao
interface UserDao {
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUser(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertName(user: User)
}