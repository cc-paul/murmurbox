package com.example.murmurbox.data.local.database

import android.content.Context
import androidx.room.Room
import com.example.murmurbox.BuildConfig

object AppDatabaseInstance {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {

            val builder = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "murmur_db"
            )

            builder.fallbackToDestructiveMigration(BuildConfig.ALLOW_DESTRUCTIVE_MIGRATION)
            val instance = builder.build()

            INSTANCE = instance
            instance
        }
    }
}