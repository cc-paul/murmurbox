package com.pagshq.murmurbox.recyclerview.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmotionData(
    val id: Int = 0,
    val emotion: String,
    val description: String,
    val backgroundColor: String,
    val borderColor: String,
    val iconName: String,
    val titleColor: String,
    val descriptionColor: String
) : Parcelable