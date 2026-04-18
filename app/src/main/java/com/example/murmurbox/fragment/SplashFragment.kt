package com.example.murmurbox.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.window.SplashScreenView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.murmurbox.MainActivity
import com.example.murmurbox.R
import com.example.murmurbox.data.local.database.AppDatabaseInstance
import com.example.murmurbox.utils.FragmentNavigation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    private lateinit var splashScreeView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        splashScreeView = inflater.inflate(R.layout.fragment_splash, container, false)

        lifecycleScope.launch {
            delay(4000)
            showDashboardScreen()
        }

        return splashScreeView
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).changeTopAndBottomColor(R.color.light_blue,true)
    }

    private fun showDashboardScreen() {
        FragmentNavigation.navigate(
            requireActivity() as AppCompatActivity,
            DashboardFragment(),
            R.id.fragment_container,
            isRoot = true
        )
    }
}