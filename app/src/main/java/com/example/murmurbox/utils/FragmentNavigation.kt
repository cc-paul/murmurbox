package com.example.murmurbox.utils

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

object FragmentNavigation {

    fun navigate(
        activity: AppCompatActivity,
        fragment: Fragment,
        containerId: Int,
        isRoot: Boolean = false
    ) {
        val fm = activity.supportFragmentManager
        val transaction = fm.beginTransaction()
            .replace(containerId, fragment, fragment::class.java.simpleName)

        if (!isRoot) {
            transaction.addToBackStack(fragment::class.java.simpleName)
        }

        transaction.commit()
    }

    fun goBack(activity: AppCompatActivity): Boolean {
        val fm = activity.supportFragmentManager

        return if (fm.backStackEntryCount > 0) {
            fm.popBackStack()
            true
        } else {
            activity.finish()
            false
        }
    }
}