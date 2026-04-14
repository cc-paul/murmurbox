package com.example.murmurbox.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "emotions")
data class Emotion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val emotion: String,
    val description: String,
    val backgroundColor: String,
    val iconName: String,
    val titleColor: String,
    val descriptionColor: String
)