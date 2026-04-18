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
        val tag = fragment::class.java.simpleName
        val transaction = fm.beginTransaction()
        val currentFragment = fm.findFragmentById(containerId)

        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }

        var newFragment = fm.findFragmentByTag(tag)

        if (newFragment == null) {
            newFragment = fragment
            transaction.add(containerId, newFragment, tag)
        } else {
            transaction.show(newFragment)
        }

        if (!isRoot) {
            transaction.addToBackStack(tag)
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

    fun openRootFragment(
        activity: AppCompatActivity,
        fragment: Fragment,
        containerId: Int
    ) {
        val fm = activity.supportFragmentManager

        fm.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)

        fm.fragments.forEach {
            if (it != null) {
                fm.beginTransaction().remove(it).commitNowAllowingStateLoss()
            }
        }

        fm.beginTransaction()
            .replace(containerId, fragment)
            .commit()
    }
}