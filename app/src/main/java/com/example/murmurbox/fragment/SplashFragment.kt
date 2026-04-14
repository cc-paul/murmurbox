package com.example.murmurbox.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.window.SplashScreenView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.murmurbox.R
import com.example.murmurbox.data.local.database.AppDatabaseInstance
import com.example.murmurbox.utils.FragmentNavigation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    private lateinit var splashScreeView: View
    private val db by lazy {
        AppDatabaseInstance.getDatabase(requireContext())
    }

    private val userDao by lazy {
        db.userDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        splashScreeView = inflater.inflate(R.layout.fragment_splash, container, false)

        lifecycleScope.launch {
            delay(2000)
            checkExistingAccount()
        }

        return splashScreeView
    }

    private fun checkExistingAccount() {
        lifecycleScope.launch {
            val isAccountExist = userDao.getUser() == 1

            if (!isAccountExist) {
                showNameScreen()
            } else {
                showDashboardScreen()
            }
        }
    }

    private fun showNameScreen() {
        FragmentNavigation.navigate(
            requireActivity() as AppCompatActivity,
            NameFragment(),
            R.id.fragment_container,
            isRoot = true
        )
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