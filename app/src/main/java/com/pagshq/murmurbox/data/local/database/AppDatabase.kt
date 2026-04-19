package com.pagshq.murmurbox.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pagshq.murmurbox.data.local.dao.EmotionDao
import com.pagshq.murmurbox.data.local.dao.UserDao
import com.pagshq.murmurbox.data.local.entity.Emotion
import com.pagshq.murmurbox.data.local.entity.User

@Database(
    entities = [
        User::class,
        Emotion::class
    ],
    version = 7
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun emotionDao(): EmotionDao
}