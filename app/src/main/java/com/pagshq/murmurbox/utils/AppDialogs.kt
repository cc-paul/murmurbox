package com.pagshq.murmurbox.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.pagshq.murmurbox.R

class AppDialogs(private val context: Context) {
    private var currentDialog: AlertDialog? = null

    fun showInputDialog(
        hint: String = "Enter text",
        emptyMessage: String = "Please provide data",
        prefill: String? = null,
        onSave: (String) -> Unit,
        onCancel: (() -> Unit)? = null
    ) {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_save_cancel, null)

        val etInput = view.findViewById<EditText>(R.id.etInput)
        val crdSave = view.findViewById<CardView>(R.id.crdSave)
        val crdDiscard = view.findViewById<CardView>(R.id.crdDiscard)

        etInput.hint = hint
        prefill?.let {
            etInput.setText(it)
            etInput.setSelection(it.length)
        }

        currentDialog = AlertDialog.Builder(context)
            .setView(view)
            .setCancelable(false)
            .create()

        crdSave.setOnClickListener {
            val text = etInput.text.toString().trim()

            if (text.isBlank()) {
                showToastMessage(emptyMessage,context)
                return@setOnClickListener
            }

            onSave(text)
            dismiss()
        }

        crdDiscard.setOnClickListener {
            onCancel?.invoke()
            dismiss()
        }

        currentDialog?.show()
    }

    fun showToastMessage(message: String,context: Context) {
        Toast.makeText(context,message, Toast.LENGTH_LONG).show()
    }


    fun dismiss() {
        currentDialog?.dismiss()
        currentDialog = null
    }
}