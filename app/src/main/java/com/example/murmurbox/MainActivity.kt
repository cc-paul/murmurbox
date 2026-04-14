package com.example.murmurbox

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.murmurbox.fragment.SplashFragment
import com.example.murmurbox.utils.FragmentNavigation

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        changeTopAndBottomColor()
        forceDarkMode()

        FragmentNavigation.navigate(
            this,
            SplashFragment(),
            R.id.fragment_container,
            isRoot = true
        )

        onBackPressedDispatcher.addCallback(this) {
            FragmentNavigation.goBack(this@MainActivity)
        }
    }

    fun forceDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    fun changeTopAndBottomColor() {
        val color = ContextCompat.getColor(this,R.color.primary)

        window.statusBarColor = color
        window.navigationBarColor = color

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
        controller.isAppearanceLightNavigationBars = true
    }
}