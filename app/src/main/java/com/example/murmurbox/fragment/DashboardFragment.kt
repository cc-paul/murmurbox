package com.example.murmurbox.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
    private lateinit var lnNew: LinearLayout

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
            lnNew = findViewById(R.id.lnNew)
        }

        crdNew.setSafeClickListener {
            showEmotionSelection()
        }

        lnNew.setSafeClickListener {
            showEmotionSelection()
        }

        return dashboardView
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).changeTopAndBottomColor(R.color.light_blue,true)
    }

    private fun showEmotionSelection() {
        FragmentNavigation.navigate(
            requireActivity() as AppCompatActivity,
            EmotionSelectionFragment(),
            R.id.fragment_container,
        )
    }
}