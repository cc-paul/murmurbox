package com.example.murmurbox.fragment

import android.os.Bundle
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
    private lateinit var lnStartStopRecord: LinearLayout
    private lateinit var lnDelete: LinearLayout
    private lateinit var tvReminder: TextView

    private lateinit var waveContainer: LinearLayout
    private lateinit var bars: List<View>

    private var waveJob: Job? = null
    private var isTalking = false

    private var id: Int = 0
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
            lnStartStopRecord = findViewById(R.id.lnStartStopRecord)
            lnDelete = findViewById(R.id.lnDelete)
            tvReminder = findViewById(R.id.tvReminder)

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
        setTalking(true)

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

    override fun onDestroyView() {
        super.onDestroyView()
        stopWaveAnimation()
    }

    companion object {
        private const val KEY_ID = "id"
        private const val KEY_COLOR = "color"
        private const val KEY_NAME = "name"

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