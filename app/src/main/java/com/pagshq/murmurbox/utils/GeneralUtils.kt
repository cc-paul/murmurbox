package com.pagshq.murmurbox.utils

import android.content.Context
import android.widget.Toast

class GeneralUtils {
    fun showToastMessage(myContext: Context, message: String) {
        Toast.makeText(myContext,message,Toast.LENGTH_SHORT).show()
    }
}