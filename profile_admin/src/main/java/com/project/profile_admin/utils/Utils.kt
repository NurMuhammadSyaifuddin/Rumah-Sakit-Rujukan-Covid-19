package com.project.profile_admin.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.profile_admin.databinding.LayoutBottomPickBinding
import com.project.profile_admin.databinding.LayoutEditNameProfileBinding

fun showAlertDialogEditName(context: Context, name: String, listenerSave: (String) -> Unit): AlertDialog {
    val binding = LayoutEditNameProfileBinding.inflate(LayoutInflater.from(context), null, false)
    val dialog = MaterialAlertDialogBuilder(context)
        .setView(binding.root)
        .setCancelable(false)
        .create()

    binding.apply {
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnSave.setOnClickListener {
            listenerSave(edtEnterName.text.toString())
            dialog.dismiss()
        }
        edtEnterName.setText(name)
        edtEnterName.setSelectAllOnFocus(true)
        edtEnterName.selectAll()
    }

    return dialog
}

fun showBottomSheetDialogEditImage(context: Context, listenerDelete: () -> Unit, listenerPickCamera: () -> Unit, listenerPickGallery: () -> Unit): BottomSheetDialog{
    val binding = LayoutBottomPickBinding.inflate(LayoutInflater.from(context), null, false)

    val dialog = BottomSheetDialog(context).apply {
        setContentView(binding.root)
        setCancelable(true)
        dismissWithAnimation = true
    }

    binding.apply {
        btnDeleteProfile.setOnClickListener {
            listenerDelete()
            dialog.dismiss()
        }
        btnPickCamera.setOnClickListener {
            listenerPickCamera()
            dialog.dismiss()
        }
        btnPickGallery.setOnClickListener {
            listenerPickGallery()
            dialog.dismiss()
        }
    }

    return dialog
}