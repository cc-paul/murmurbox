package com.pagshq.murmurbox.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.pagshq.murmurbox.R
import com.pagshq.murmurbox.data.local.database.AppDatabaseInstance
import com.pagshq.murmurbox.data.local.entity.User
import com.pagshq.murmurbox.utils.FragmentNavigation
import com.pagshq.murmurbox.utils.GeneralUtils
import com.pagshq.murmurbox.utils.setSafeClickListener
import kotlinx.coroutines.launch

class NameFragment : Fragment() {
    private lateinit var nameView: View
    private lateinit var crdContinue: CardView
    private lateinit var etName: EditText
    private val generalUtils = GeneralUtils()

    private val db by lazy {
        AppDatabaseInstance.getDatabase(requireContext())
    }

    private val userDao by lazy {
        db.userDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        nameView = inflater.inflate(R.layout.fragment_name, container, false)

        nameView.apply {
            crdContinue = findViewById(R.id.crdContinue)
            etName = findViewById(R.id.etName)
        }

        crdContinue.setSafeClickListener {
            if (etName.text.isEmpty()) {
                generalUtils.showToastMessage(requireContext(),"Please provide your name")
            } else {
                saveNameAndGoToDashboard()
            }
        }

        return nameView
    }

    private fun saveNameAndGoToDashboard() {
        lifecycleScope.launch {
            userDao.insertName(User(
                id = 1,
                name = etName.text.toString()
            ))

            FragmentNavigation.navigate(
                requireActivity() as AppCompatActivity,
                DashboardFragment(),
                R.id.fragment_container,
                isRoot = true
            )
        }
    }
}