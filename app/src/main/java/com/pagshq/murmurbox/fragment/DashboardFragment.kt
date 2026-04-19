package com.pagshq.murmurbox.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.pagshq.murmurbox.MainActivity
import com.pagshq.murmurbox.R
import com.pagshq.murmurbox.utils.FragmentNavigation
import com.pagshq.murmurbox.utils.setSafeClickListener

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


        setDefaultColor()
        return dashboardView
    }

    override fun onResume() {
        super.onResume()
        setDefaultColor()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            setDefaultColor()
        }
    }

    private fun setDefaultColor() {
        (activity as? MainActivity)?.changeTopAndBottomColor(
            R.color.light_blue,
            true
        )
    }

    private fun showEmotionSelection() {
        FragmentNavigation.navigate(
            requireActivity() as AppCompatActivity,
            EmotionSelectionFragment(),
            R.id.fragment_container,
        )
    }
}