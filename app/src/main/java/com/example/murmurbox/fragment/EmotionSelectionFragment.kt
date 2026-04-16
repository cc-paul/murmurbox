package com.example.murmurbox.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
import com.example.murmurbox.MainActivity
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
    private lateinit var lnBack: LinearLayout
    private lateinit var lnNext: LinearLayout
    private lateinit var crdContinue: CardView
    private lateinit var emotionAdapter: EmotionAdapter
    private lateinit var frContainer: FrameLayout
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
            lnBack = findViewById(R.id.lnBack)
            lnNext = findViewById(R.id.lnNext)
            crdContinue = findViewById(R.id.crdContinue)
            frContainer = findViewById(R.id.frContainer)
        }

        loadAllEmotions()

        lnBack.setSafeClickListener {
            FragmentNavigation.goBack(requireActivity() as AppCompatActivity)
        }

        crdContinue.setSafeClickListener {
            if (selectedEmotion.id != 0) {
                gotoRecording()
            }
        }

        lnNext.setSafeClickListener {
            if (selectedEmotion.id != 0) {
                gotoRecording()
            }
        }

        (activity as MainActivity).changeTopAndBottomColor(R.color.light_blue,true)

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
                    descriptionColor = item.descriptionColor,
                    borderColor = item.borderColor
                )
            })

            rvEmotions.layoutManager = GridLayoutManager(requireContext(),3)
            emotionAdapter = EmotionAdapter(emotionList, onItemClick = { item ->
                selectedEmotion.apply {
                    id = item.id
                    emotion = item.emotion
                    emotionColor = item.emotionColor
                    backgroundColor = item.backgroundColor
                }

                (activity as MainActivity).changeTopAndBottomColor(selectedEmotion.backgroundColor.toColorInt(),true)
                frContainer.setBackgroundColor(selectedEmotion.backgroundColor.toColorInt())
                crdContinue.visibility = View.VISIBLE
                lnNext.visibility = View.VISIBLE
            })
            rvEmotions.adapter = emotionAdapter
            crdContinue.visibility = View.GONE
            lnNext.visibility = View.GONE
        }
    }
}