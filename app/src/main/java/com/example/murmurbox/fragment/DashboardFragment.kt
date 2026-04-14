package com.example.murmurbox.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.murmurbox.R
import com.example.murmurbox.data.local.database.AppDatabaseInstance
import com.example.murmurbox.data.local.entity.Emotion
import com.example.murmurbox.utils.FragmentNavigation
import com.example.murmurbox.utils.setSafeClickListener
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {
    private lateinit var dashboardView: View
    private lateinit var crdNew: CardView
    private val db by lazy {
        AppDatabaseInstance.getDatabase(requireContext())
    }
    private val emotionDao by lazy {
        db.emotionDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardView = inflater.inflate(R.layout.fragment_dashboard, container, false)

        dashboardView.apply {
            crdNew = findViewById(R.id.crdNew)
        }

        crdNew.setSafeClickListener {
            showEmotionSelection()
        }

        return dashboardView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadEmotions()
    }

    private fun showEmotionSelection() {
        FragmentNavigation.navigate(
            requireActivity() as AppCompatActivity,
            EmotionSelectionFragment(),
            R.id.fragment_container,
        )
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