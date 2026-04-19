package com.pagshq.murmurbox.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.media.MediaRecorder
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pagshq.murmurbox.MainActivity
import com.pagshq.murmurbox.R
import com.pagshq.murmurbox.recyclerview.adapter.WaveAdapter
import com.pagshq.murmurbox.recyclerview.data.WaveData
import com.pagshq.murmurbox.utils.FragmentNavigation
import com.pagshq.murmurbox.utils.setSafeClickListener
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.pagshq.murmurbox.entity.RecordingResult
import com.pagshq.murmurbox.utils.AppDialogs
import java.io.File

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
    private lateinit var dialogs: AppDialogs
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
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    private var isRecording = false
    private var isPaused = false

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
            PermissionX.init(this)
            .permissions(Manifest.permission.RECORD_AUDIO)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    when(recordMode) {
                        RECORD_NOT_STARTED -> {
                            recordMode = RECORD_STARTED

                            startWaveAnimation()
                            startTimer()
                            startRecording(requireContext())
                        }
                        RECORD_STARTED -> {
                            if (isRunning) {
                                pauseTimer()
                                stopWaveAnimation()
                                pauseRecording()
                            } else{
                                startTimer()
                                startWaveAnimation()
                                resumeRecording()
                            }
                        }
                    }

                    changeOtherRecordButtonVisibility()
                    changeRecordStatusLabel()
                    changeRecordButtonUI()
                } else {
                    if (deniedList.isNotEmpty()) {
                        showPermissionDialog()
                    }
                }
            }
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
            discardRecording()
        }

        crdDone.setSafeClickListener {
            if (isRunning) {
                lnRecordPlayPause.performClick()
            }
            stopRecording {
                dialogs.showInputDialog(
                    hint = "Enter Record Name",
                    emptyMessage = "Please provide record name",
                    onSave = { name ->
                        val result = saveRecording()

                        if (result == null) {
                            dialogs.showToastMessage("Recording is Empty. Please try again later",requireContext())
                            return@showInputDialog
                        }
                    },
                    onCancel = {
                        FragmentNavigation.openRootFragment(
                            requireActivity() as AppCompatActivity,
                            DashboardFragment(),
                            R.id.fragment_container,
                        )
                    }
                )
            }
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
        discardRecording()

        rvWave.adapter = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialogs = AppDialogs(requireContext())
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

    private fun showPermissionDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage("Microphone access is needed to record voice.")
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = "package:${requireContext().packageName}".toUri()
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun startRecording(context: Context) {

        val dir = File(context.filesDir, "recordings")
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val file = File(
            dir,
            "rec_${System.currentTimeMillis()}.m4a"
        )

        outputFile = file

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)

            prepare()
            start()
        }

        isRecording = true
        isPaused = false
    }

    private fun pauseRecording() {
        recorder?.pause()
        isPaused = true
    }

    private fun resumeRecording() {
        recorder?.resume()
        isPaused = false
    }

    private fun stopRecording(onSaved: (File?) -> Unit) {
        try {
            if (isRecording) {
                recorder?.stop()
            }
        } catch (e: Exception) {
        }

        recorder?.release()
        recorder = null

        isRecording = false
        isPaused = false

        onSaved(outputFile)
    }

    private fun discardRecording() {
        try {
            if (isRecording) {
                recorder?.stop()
            }
        } catch (e: Exception) {
        }

        try {
            recorder?.release()
        } catch (e: Exception) {
        }

        recorder = null

        outputFile?.delete()
        outputFile = null

        isRecording = false
        isPaused = false
    }

    private fun saveRecording(): RecordingResult? {
        val file = outputFile ?: return null
        if (!file.exists()) return null
        if (file.length() <= 0) return null
        val name = file.nameWithoutExtension

        return RecordingResult(
            file = file,
            name = name
        )
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