package com.pagshq.murmurbox.utils

import android.view.View

fun View.setSafeClickListener(interval: Long = 1000, onClick: (View) -> Unit) {
    var lastClickTime = 0L

    setOnClickListener {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastClickTime < interval) return@setOnClickListener

        lastClickTime = currentTime
        onClick(it)
    }
}