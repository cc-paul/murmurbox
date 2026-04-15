package com.example.murmurbox.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.murmurbox.MainActivity
import com.example.murmurbox.R
import com.example.murmurbox.data.local.database.AppDatabaseInstance
import com.example.murmurbox.data.local.entity.Emotion
import com.example.murmurbox.utils.FragmentNavigation
import com.example.murmurbox.utils.setSafeClickListener
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {
    private lateinit var dashboardView: View
    private lateinit var crdNew: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as MainActivity).changeTopAndBottomColor(R.color.light_blue,true)
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
    }

    private fun showEmotionSelection() {
        FragmentNavigation.navigate(
            requireActivity() as AppCompatActivity,
            EmotionSelectionFragment(),
            R.id.fragment_container,
        )
    }
}