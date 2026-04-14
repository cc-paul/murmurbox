package com.example.murmurbox.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.murmurbox.data.local.dao.EmotionDao
import com.example.murmurbox.data.local.dao.UserDao
import com.example.murmurbox.data.local.entity.Emotion
import com.example.murmurbox.data.local.entity.User

@Database(
    entities = [
        User::class,
        Emotion::class
    ],
    version = 5
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun emotionDao(): EmotionDao
}