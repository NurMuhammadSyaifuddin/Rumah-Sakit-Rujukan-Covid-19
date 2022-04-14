package com.project.profile_hospital.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.profile_hospital.databinding.LayoutBottomPickImageProfileBinding
import com.project.profile_hospital.databinding.LayoutEditNameBinding

fun showAlertDialogEditName(context: Context, name: String, titleDialog: String? = null, listenerSave: (String) -> Unit): AlertDialog {
    val binding = LayoutEditNameBinding.inflate(LayoutInflater.from(context), null, false)
    val dialog = MaterialAlertDialogBuilder(context)
        .setView(binding.root)
        .setCancelable(false)
        .create()

    binding.apply {
        if (titleDialog != null){
            textViewEnterName.text = titleDialog
        }
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

fun showBottomSheetDialogEditImage(context: Context, listenerPickCamera: () -> Unit, listenerPickGallery: () -> Unit): BottomSheetDialog{
    val binding = LayoutBottomPickImageProfileBinding.inflate(LayoutInflater.from(context), null, false)

    val dialog = BottomSheetDialog(context).apply {
        setContentView(binding.root)
        setCancelable(true)
        dismissWithAnimation = true
    }

    binding.apply {
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