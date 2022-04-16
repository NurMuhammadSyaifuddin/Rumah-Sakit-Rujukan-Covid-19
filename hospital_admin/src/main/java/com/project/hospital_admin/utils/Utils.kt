package com.project.hospital_admin.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.hospital_admin.databinding.LayoutAlertCheckingRegistrationBinding

fun showAlertDialogCheckingRegistration(context: Context, message: String, listenerNext: () -> Unit): AlertDialog {
    val binding =
        LayoutAlertCheckingRegistrationBinding.inflate(LayoutInflater.from(context), null, false)
    val dialog = MaterialAlertDialogBuilder(context)
        .setView(binding.root)
        .setCancelable(false)
        .create()

    binding.apply {
        tvChekcingRegistration.text = message
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnNext.setOnClickListener {
            dialog.dismiss()
            listenerNext()
        }
    }

    return dialog
}