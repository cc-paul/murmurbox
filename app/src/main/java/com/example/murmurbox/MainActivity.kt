package com.example.murmurbox

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.example.murmurbox.data.local.database.AppDatabaseInstance
import com.example.murmurbox.data.local.entity.Emotion
import com.example.murmurbox.fragment.SplashFragment
import com.example.murmurbox.utils.FragmentNavigation
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val db by lazy {
        AppDatabaseInstance.getDatabase(this)
    }
    private val emotionDao by lazy {
        db.emotionDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        forceDarkMode()
        loadEmotions()

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

    private fun forceDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    fun changeTopAndBottomColor(colorRes: Int, isLightColor: Boolean) {
        val resolvedColor = ContextCompat.getColor(this, colorRes)

        window.statusBarColor = resolvedColor
        window.navigationBarColor = resolvedColor

        val controller = WindowInsetsControllerCompat(window, window.decorView)

        controller.isAppearanceLightStatusBars = isLightColor
        controller.isAppearanceLightNavigationBars = isLightColor
    }

    private fun loadEmotions() {
        lifecycleScope.launch {
            val emotions = listOf(
                Emotion(
                    id = 1,
                    emotion = "Happy",
                    description = "Joyful, grateful",
                    backgroundColor = "#f2f7ea",
                    iconName = "ic_happy",
                    titleColor = "#27500A",
                    descriptionColor = "#3B6D11"
                ),
                Emotion(
                    id = 2,
                    emotion = "Sad",
                    description = "Heavy, tearful",
                    backgroundColor = "#eaf2fb",
                    iconName = "ic_sad",
                    titleColor = "#0C447C",
                    descriptionColor = "#185FA5"
                ),
                Emotion(
                    id = 3,
                    emotion = "Anxious",
                    description = "Restless, tense",
                    backgroundColor = "#fdf4e7",
                    iconName = "ic_anxious",
                    titleColor = "#633806",
                    descriptionColor = "#854F0B"
                ),
                Emotion(
                    id = 4,
                    emotion = "Angry",
                    description = "Frustrated, mad",
                    backgroundColor = "#fdf0eb",
                    iconName = "ic_angry",
                    titleColor = "#712B13",
                    descriptionColor = "#993C1D"
                ),
                Emotion(
                    id = 5,
                    emotion = "Lonely",
                    description = "Isolated, unseen",
                    backgroundColor = "#f0effe",
                    iconName = "ic_lonely",
                    titleColor = "#26215C",
                    descriptionColor = "#534AB7"
                ),
                Emotion(
                    id = 6,
                    emotion = "Calm",
                    description = "Peaceful, still",
                    backgroundColor = "#e8f8f3",
                    iconName = "ic_calm",
                    titleColor = "#085041",
                    descriptionColor = "#0F6E56"
                )
            )

            emotionDao.insertAll(emotions)
        }
    }
}