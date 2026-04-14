package com.example.murmurbox.fragment

import android.annotation.SuppressLint
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.murmurbox.R
import com.example.murmurbox.utils.FragmentNavigation
import com.example.murmurbox.utils.setSafeClickListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RecordingFragment : Fragment() {

    private lateinit var recordingView: View

    private lateinit var tvBack: TextView
    private lateinit var imgEmotionStatus: ImageView
    private lateinit var tvSelectedEmotion: TextView
    private lateinit var tvRecordDuration: TextView
    private lateinit var lnPlayPause: LinearLayout
    private lateinit var imgPlayPause: ImageView
    private lateinit var lnStartStopSaveRecord: LinearLayout
    private lateinit var lnDelete: LinearLayout
    private lateinit var tvRecordReminder: TextView
    private lateinit var tvRecordStatus: TextView
    private lateinit var waveContainer: LinearLayout
    private lateinit var imgStartStopSaveRecord: ImageView
    private lateinit var bars: List<View>

    private var waveJob: Job? = null
    private var timerJob: Job? = null
    private var isTalking = false
    private var isPlaying = false
    private var hasRecord = false
    private var seconds = 0
    private var id: Int = 0
    private var currentRecordStatus = START_RECORDING
    private var emotionColor: String = ""
    private var emotionName: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            id = it.getInt(KEY_ID)
            emotionColor = it.getString(KEY_COLOR).toString()
            emotionName = it.getString(KEY_NAME).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        recordingView = inflater.inflate(R.layout.fragment_recording, container, false)

        recordingView.apply {
            tvBack = findViewById(R.id.tvBack)
            imgEmotionStatus = findViewById(R.id.imgEmotionStatus)
            tvSelectedEmotion = findViewById(R.id.tvSelectedEmotion)
            tvRecordDuration = findViewById(R.id.tvRecordDuration)
            lnPlayPause = findViewById(R.id.lnPlayPause)
            imgPlayPause = findViewById(R.id.imgPlayPause)
            lnStartStopSaveRecord = findViewById(R.id.lnStartStopSaveRecord)
            imgStartStopSaveRecord = findViewById(R.id.imgStartStopSaveRecord)
            lnDelete = findViewById(R.id.lnDelete)
            tvRecordReminder = findViewById(R.id.tvRecordReminder)
            tvRecordStatus = findViewById(R.id.tvRecordStatus)
            waveContainer = findViewById(R.id.waveContainer)
            bars = listOf(
                findViewById(R.id.bar1),
                findViewById(R.id.bar2),
                findViewById(R.id.bar3),
                findViewById(R.id.bar4),
                findViewById(R.id.bar5),
                findViewById(R.id.bar6),
                findViewById(R.id.bar7),
                findViewById(R.id.bar8),
                findViewById(R.id.bar9),
                findViewById(R.id.bar10)
            )
        }

        tvBack.setSafeClickListener {
            FragmentNavigation.goBack(requireActivity() as AppCompatActivity)
        }

        getSelectedEmotion()
        stopTimer()
        stopWaveAnimation()
        setTalking(isTalking)
        changeRecordStatusTitle()
        changeRecordReminder(currentRecordStatus)
        changeSubButtonAlpha()

        lnStartStopSaveRecord.setSafeClickListener {
            when (currentRecordStatus) {
                START_RECORDING -> {
                    isTalking = true
                    currentRecordStatus = DURING_RECORDING
                }
                DURING_RECORDING -> {
                    isTalking = false
                    hasRecord = true
                    currentRecordStatus = SAVE_RECORDING
                }
            }

            if (isTalking) {
                startTimer()
                startWaveAnimation()
            } else {
                stopTimer()
                stopWaveAnimation()
            }

            setTalking(isTalking)
            changeRecordStatusTitle()
            changeRecordReminder(currentRecordStatus)
            changeSubButtonAlpha()
        }

        lnPlayPause.setOnClickListener {
            if (hasRecord) {
                isPlaying = !isPlaying
                changePlayPauseButtonIcon()
            }
        }

        lnDelete.setOnClickListener {
            if (hasRecord) {

            }
        }

        return recordingView
    }

    private fun getSelectedEmotion() {
        tvSelectedEmotion.text = emotionName
        imgEmotionStatus.setColorFilter(emotionColor.toColorInt())
    }

    fun setTalking(talking: Boolean) {
        isTalking = talking

        if (talking) {
            waveContainer.visibility = View.VISIBLE
            startWaveAnimation()
        } else {
            waveContainer.visibility = View.GONE
            stopWaveAnimation()
        }
    }

    private fun startWaveAnimation() {
        waveJob?.cancel()

        waveJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isTalking) {
                bars.forEach { bar ->
                    val height = (30..50).random()
                    val params = bar.layoutParams
                    params.height = height
                    bar.layoutParams = params
                }
                delay(60)
            }
        }
    }

    private fun stopWaveAnimation() {
        waveJob?.cancel()
        waveJob = null
    }

    @SuppressLint("SetTextI18n")
    private fun startTimer() {
        timerJob?.cancel()
        seconds = 0

        timerJob = viewLifecycleOwner.lifecycleScope.launch {
            while (true) {
                val minutes = seconds / 60
                val secs = seconds % 60

                tvRecordDuration.text = "${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"

                seconds++
                delay(1000)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        seconds = 0
        tvRecordDuration.text = "00:00"
    }

    private fun changeRecordStatusTitle() {
        if (isTalking) {
            tvRecordReminder.text = "recording"
        } else {
            tvRecordReminder.text = "record not started"
        }
    }

    private fun changeRecordReminder(mode: Int) {
        when (mode) {

            START_RECORDING -> {
                tvRecordReminder.text = "tap circle to start recording"

                imgStartStopSaveRecord.setImageDrawable(null)
                imgStartStopSaveRecord.setImageResource(R.drawable.circle_white_bg)
            }

            DURING_RECORDING -> {
                tvRecordReminder.text = "tap square to finish"

                imgStartStopSaveRecord.setImageDrawable(null)
                imgStartStopSaveRecord.setImageResource(R.drawable.ic_rounded_square)
            }

            SAVE_RECORDING -> {
                tvRecordReminder.text =
                    "tap to save your recording or go back here again to start a new one"

                imgStartStopSaveRecord.setImageDrawable(null)
                imgStartStopSaveRecord.setImageResource(R.drawable.ic_save)
            }
        }

        imgStartStopSaveRecord.invalidate()
    }

    private fun changeSubButtonAlpha() {
        if (hasRecord) {
            lnPlayPause.alpha = 1f
            lnDelete.alpha = 1f
        } else {
            lnPlayPause.alpha = 0.5f
            lnDelete.alpha = 0.5f
        }
    }

    private fun changePlayPauseButtonIcon() {
        imgPlayPause.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopWaveAnimation()
        stopTimer()
    }

    companion object {
        private const val KEY_ID = "id"
        private const val KEY_COLOR = "color"
        private const val KEY_NAME = "name"
        private const val START_RECORDING = 1
        private const val DURING_RECORDING = 2
        private const val SAVE_RECORDING = 3

        fun newInstance(
            id: Int,
            emotionColor: String,
            emotionName: String
        ): RecordingFragment {
            return RecordingFragment().apply {
                arguments = Bundle().apply {
                    putInt(KEY_ID, id)
                    putString(KEY_COLOR, emotionColor)
                    putString(KEY_NAME, emotionName)
                }
            }
        }
    }
}