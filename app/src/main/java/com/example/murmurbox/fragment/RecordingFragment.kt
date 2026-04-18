package com.example.murmurbox.fragment

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.murmurbox.MainActivity
import com.example.murmurbox.R
import com.example.murmurbox.recyclerview.adapter.WaveAdapter
import com.example.murmurbox.recyclerview.data.WaveData
import com.example.murmurbox.utils.FragmentNavigation
import com.example.murmurbox.utils.setSafeClickListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.Text

class RecordingFragment : Fragment() {
    private lateinit var recordingView: View
    private lateinit var frRecodingView: View
    private lateinit var lnBack: LinearLayout
    private lateinit var tvEmotionName: TextView
    private lateinit var crdEmotionBorder: CardView
    private lateinit var lnCircle: LinearLayout
    private lateinit var crdEmotionBackground: CardView
    private lateinit var rvWave: RecyclerView
    private lateinit var tvTimer: TextView
    private lateinit var tvRecordStatus: TextView
    private lateinit var lnRecordPlayPause: LinearLayout
    private lateinit var imgRecordPlayPause: ImageView
    private lateinit var tvPressMic: TextView
    private lateinit var lnOtherButtons: LinearLayout
    private lateinit var crdRestart: CardView
    private lateinit var crdDone: CardView
    private lateinit var waveAdapter: WaveAdapter
    private var id: Int = 0
    private var backgroundColor: String = ""
    private var borderColor: String = ""
    private var name: String = ""
    private var waveJob: Job? = null
    private var timerJob: Job? = null
    private var elapsedTime = 0L
    private var isRunning = false
    private var recordMode = RECORD_NOT_STARTED
    private var defaultWaveColor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            id = it.getInt(KEY_ID)
            backgroundColor = it.getString(KEY_BACKGROUND_COLOR).orEmpty()
            borderColor = it.getString(KEY_BORDER_COLOR).orEmpty()
            name = it.getString(KEY_NAME).orEmpty()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recordingView = inflater.inflate(R.layout.fragment_recording, container, false)
        defaultWaveColor = ContextCompat.getColor(requireContext(), R.color.wave_gray)

        recordingView.apply {
            frRecodingView = findViewById(R.id.frRecodingView)
            lnBack = findViewById(R.id.lnBack)
            tvEmotionName = findViewById(R.id.tvEmotionName)
            lnCircle = findViewById(R.id.lnCircle)
            crdEmotionBorder = findViewById(R.id.crdEmotionBorder)
            crdEmotionBackground = findViewById(R.id.crdEmotionBackground)
            rvWave = findViewById(R.id.rvWave)
            tvTimer = findViewById(R.id.tvTimer)
            tvRecordStatus = findViewById(R.id.tvRecordStatus)
            lnRecordPlayPause = findViewById(R.id.lnRecordPlayPause)
            imgRecordPlayPause = findViewById(R.id.imgRecordPlayPause)
            tvPressMic = findViewById(R.id.tvPressMic)
            lnOtherButtons = findViewById(R.id.lnOtherButtons)
            crdRestart = findViewById(R.id.crdRestart)
            crdDone = findViewById(R.id.crdDone)
        }

        lnBack.setSafeClickListener {
            FragmentNavigation.goBack(requireActivity() as AppCompatActivity)
        }

        setupWave()
        changeEmotionLabel()
        changeRecordStatusLabel()
        changeRecordButtonUI()
        changeOtherRecordButtonVisibility()

        lnRecordPlayPause.setSafeClickListener {
            when(recordMode) {
                RECORD_NOT_STARTED -> {
                    recordMode = RECORD_STARTED

                    startWaveAnimation()
                    startTimer()
                }
                RECORD_STARTED -> {
                    if (isRunning) {
                        pauseTimer()
                        stopWaveAnimation()
                    } else{
                        startTimer()
                        startWaveAnimation()
                    }
                }
            }

            changeOtherRecordButtonVisibility()
            changeRecordStatusLabel()
            changeRecordButtonUI()
        }

        crdRestart.setSafeClickListener {
            recordMode = RECORD_NOT_STARTED


            changeEmotionLabel()
            changeRecordStatusLabel()
            changeRecordButtonUI()
            changeOtherRecordButtonVisibility()
            stopWaveAnimation()
            resetWave()
            resetTimer()
        }

        crdDone.setSafeClickListener {
            Toast.makeText(requireContext(),"Record has been saved successfully", Toast.LENGTH_LONG).show()

            FragmentNavigation.openRootFragment(
                requireActivity() as AppCompatActivity,
                DashboardFragment(),
                R.id.fragment_container
            )
        }

        return recordingView
    }

    override fun onResume() {
        super.onResume()
        changeModuleColor()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        stopWaveAnimation()
        resetWave()
        pauseTimer()

        rvWave.adapter = null
    }

    private fun changeRecordStatusLabel() {
        when (recordMode) {
            RECORD_NOT_STARTED -> {
                tvRecordStatus.text = "Tap to start"
            }
            RECORD_STARTED -> {
                tvRecordStatus.text = "Recording..."
            }
        }
    }

    private fun changeOtherRecordButtonVisibility() {
        when (recordMode) {
            RECORD_NOT_STARTED -> {
                tvPressMic.visibility = View.VISIBLE
                lnOtherButtons.visibility = View.GONE
            }
            RECORD_STARTED -> {
                tvPressMic.visibility = View.GONE
                lnOtherButtons.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun changeRecordButtonUI() {
        var recordButtonColor: Int = R.color.dark_blue
        var recordButtonImage: Int = R.drawable.ic_mic_white

        when (recordMode) {
            RECORD_NOT_STARTED -> {
                recordButtonColor = ContextCompat.getColor(requireContext(), R.color.dark_blue)
                recordButtonImage = R.drawable.ic_mic_white
            }
            RECORD_STARTED -> {
                recordButtonColor = borderColor.toColorInt()

                recordButtonImage = if (isRunning) {
                    R.drawable.ic_pause_white
                } else {
                    R.drawable.ic_play_white
                }
            }
        }

        val drawable = lnRecordPlayPause.background.mutate()
        drawable.setTint(recordButtonColor)
        lnRecordPlayPause.background = drawable
        imgRecordPlayPause.setImageResource(recordButtonImage)
    }

    private fun changeEmotionLabel() {
        tvEmotionName.text = name
        lnCircle.backgroundTintList = ColorStateList.valueOf(borderColor.toColorInt())
        crdEmotionBorder.backgroundTintList = ColorStateList.valueOf(borderColor.toColorInt())
        crdEmotionBackground.backgroundTintList = ColorStateList.valueOf(backgroundColor.toColorInt())
    }

    private fun setupWave() {
        val waveList = ArrayList<WaveData>()

        repeat(30) {
            waveList.add(WaveData(height = 40,defaultWaveColor))
        }

        rvWave.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        waveAdapter = WaveAdapter(waveList,defaultWaveColor)
        rvWave.adapter = waveAdapter
    }

    private fun startWaveAnimation() {
        waveJob?.cancel()

        waveJob = viewLifecycleOwner.lifecycleScope.launch {
            while (true) {
                waveAdapter.animateWave(borderColor.toColorInt())
                delay(100)
            }
        }
    }

    private fun stopWaveAnimation() {
        waveJob?.cancel()
        waveJob = null
    }

    private fun resetWave() {
        waveAdapter.reset(defaultWaveColor)
    }

    private fun startTimer() {
        if (isRunning) return

        isRunning = true
        waveAdapter.setPaused(false)

        timerJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isRunning) {
                updateTimerUI()
                delay(1000)
                elapsedTime += 1000
            }
        }
    }

    private fun pauseTimer() {
        isRunning = false
        timerJob?.cancel()

        waveAdapter.setPaused(true)
    }

    private fun resetTimer() {
        pauseTimer()
        elapsedTime = 0
        updateTimerUI()
    }

    @SuppressLint("DefaultLocale")
    private fun updateTimerUI() {
        val seconds = (elapsedTime / 1000) % 60
        val minutes = (elapsedTime / (1000 * 60)) % 60

        tvTimer.text = String.format("%02d : %02d", minutes, seconds)
    }

    private fun changeModuleColor() {
        frRecodingView.setBackgroundColor(backgroundColor.toColorInt())
        changeTopAndBottomColor(backgroundColor,true)
    }

    private fun changeTopAndBottomColor(color: String,isLightColor: Boolean = false) {
        if (color == "") return

        val resColor = color.toColorInt()
        (activity as MainActivity).changeTopAndBottomColor(resColor,isLightColor)
    }

    companion object {
        private const val KEY_ID = "id"
        private const val KEY_BACKGROUND_COLOR = "backgroundColor"
        private const val KEY_BORDER_COLOR = "borderColor"
        private const val KEY_NAME = "name"
        private const val RECORD_NOT_STARTED = 1
        private const val RECORD_STARTED = 2

        fun newInstance(
            id: Int,
            backgroundColor: String,
            borderColor: String,
            name: String
        ): RecordingFragment {
            return RecordingFragment().apply {
                arguments = Bundle().apply {
                    putInt(KEY_ID, id)
                    putString(KEY_BACKGROUND_COLOR, backgroundColor)
                    putString(KEY_BORDER_COLOR, borderColor)
                    putString(KEY_NAME, name)
                }
            }
        }
    }
}