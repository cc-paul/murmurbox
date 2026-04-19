package com.pagshq.murmurbox

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.pagshq.murmurbox.data.local.database.AppDatabaseInstance
import com.pagshq.murmurbox.data.local.entity.Emotion
import com.pagshq.murmurbox.fragment.SplashFragment
import com.pagshq.murmurbox.utils.FragmentNavigation
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

    fun changeTopAndBottomColor(colorInput: Int, isLightColor: Boolean) {
        val resolvedColor = try {
            ContextCompat.getColor(this, colorInput)
        } catch (e: Exception) {
            colorInput
        }

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
                    borderColor = "#faa261",
                    backgroundColor = "#ffe3c6",
                    iconName = "ic_happy",
                    titleColor = "#faa261",
                    descriptionColor = "#ffe3c6"
                ),
                Emotion(
                    id = 2,
                    emotion = "Sad",
                    description = "Heavy, tearful",
                    borderColor = "#5090f6",
                    backgroundColor = "#cfe2fd",
                    iconName = "ic_sad",
                    titleColor = "#0C447C",
                    descriptionColor = "#185FA5"
                ),
                Emotion(
                    id = 3,
                    emotion = "Anxious",
                    description = "Restless, tense",
                    borderColor = "#f7be53",
                    backgroundColor = "#fdecb8",
                    iconName = "ic_anxious",
                    titleColor = "#633806",
                    descriptionColor = "#854F0B"
                ),
                Emotion(
                    id = 4,
                    emotion = "Angry",
                    description = "Frustrated, mad",
                    borderColor = "#f48282",
                    backgroundColor = "#fdd5d5",
                    iconName = "ic_angry",
                    titleColor = "#712B13",
                    descriptionColor = "#993C1D"
                ),
                Emotion(
                    id = 5,
                    emotion = "Lonely",
                    description = "Isolated, unseen",
                    borderColor = "#9e79f7",
                    backgroundColor = "#e5defd",
                    iconName = "ic_lonely",
                    titleColor = "#26215C",
                    descriptionColor = "#534AB7"
                ),
                Emotion(
                    id = 6,
                    emotion = "Calm",
                    description = "Peaceful, still",
                    borderColor = "#69d6af",
                    backgroundColor = "#c2f5dd",
                    iconName = "ic_calm",
                    titleColor = "#085041",
                    descriptionColor = "#0F6E56"
                )
            )

            emotionDao.insertAll(emotions)
        }
    }
}