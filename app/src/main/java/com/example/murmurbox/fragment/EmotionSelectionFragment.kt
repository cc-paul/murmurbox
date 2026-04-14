package com.example.murmurbox.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.murmurbox.R
import com.example.murmurbox.data.local.database.AppDatabaseInstance
import com.example.murmurbox.entity.SelectedEmotionData
import com.example.murmurbox.recyclerview.adapter.EmotionAdapter
import com.example.murmurbox.recyclerview.data.EmotionData
import com.example.murmurbox.utils.FragmentNavigation
import com.example.murmurbox.utils.setSafeClickListener
import kotlinx.coroutines.launch
import java.util.Locale


class EmotionSelectionFragment : Fragment() {
    private lateinit var emotionSelectionView: View
    private lateinit var rvEmotions: RecyclerView
    private lateinit var imgEmotionStatus: ImageView
    private lateinit var tvSelectedEmotion: TextView
    private lateinit var tvBack: TextView
    private lateinit var lnSelectedEmotion: LinearLayout
    private lateinit var crdContinue: CardView
    private lateinit var emotionAdapter: EmotionAdapter
    private val emotionList = ArrayList<EmotionData>()
    private val selectedEmotion = SelectedEmotionData()

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
        emotionSelectionView =  inflater.inflate(R.layout.fragment_emotion_selection, container, false)

        emotionSelectionView.apply {
            rvEmotions = findViewById(R.id.rvEmotions)
            imgEmotionStatus = findViewById(R.id.imgEmotionStatus)
            tvSelectedEmotion = findViewById(R.id.tvSelectedEmotion)
            lnSelectedEmotion = findViewById(R.id.lnSelectedEmotion)
            tvBack = findViewById(R.id.tvBack)
            crdContinue = findViewById(R.id.crdContinue)
        }

        loadAllEmotions()

        tvBack.setSafeClickListener {
            FragmentNavigation.goBack(requireActivity() as AppCompatActivity)
        }

        crdContinue.setSafeClickListener {
            if (selectedEmotion.id != 0) {
                gotoRecording()
            }
        }

        return emotionSelectionView
    }

    private fun gotoRecording() {
        FragmentNavigation.navigate(
            requireActivity() as AppCompatActivity,
            RecordingFragment.newInstance(selectedEmotion.id,selectedEmotion.emotionColor,selectedEmotion.emotion),
            R.id.fragment_container
        )
    }

    @SuppressLint("SetTextI18n")
    private fun loadAllEmotions() {
        lifecycleScope.launch {
            val emotions = emotionDao.getAll()

            emotionList.clear()
            emotionList.addAll(emotions.map { item ->
                EmotionData(
                    id = item.id,
                    emotion = item.emotion,
                    description = item.description,
                    backgroundColor = item.backgroundColor,
                    iconName = item.iconName,
                    titleColor = item.titleColor,
                    descriptionColor = item.descriptionColor
                )
            })

            rvEmotions.layoutManager = GridLayoutManager(requireContext(),2)
            emotionAdapter = EmotionAdapter(emotionList, onItemClick = { item ->
                selectedEmotion.apply {
                    id = item.id
                    emotion = item.emotion
                    emotionColor = item.titleColor
                }

                tvSelectedEmotion.text = "feeling: ${selectedEmotion.emotion.lowercase()}"
                imgEmotionStatus.setColorFilter(selectedEmotion.emotionColor.toColorInt())
                lnSelectedEmotion.visibility = View.VISIBLE
            })
            rvEmotions.adapter = emotionAdapter
            lnSelectedEmotion.visibility = View.GONE
        }
    }
}