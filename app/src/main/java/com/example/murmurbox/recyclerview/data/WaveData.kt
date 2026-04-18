package com.example.murmurbox.recyclerview.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WaveData(
    var height: Int,
    var color: Int
) : Parcelable