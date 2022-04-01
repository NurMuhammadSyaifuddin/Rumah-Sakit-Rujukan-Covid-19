package com.project.rumahsakitrujukancovid_19.utils

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.project.rumahsakitrujukancovid_19.R
import com.project.rumahsakitrujukancovid_19.databinding.LayoutAlertSignUpBinding
import com.project.rumahsakitrujukancovid_19.databinding.LayoutAlreadyRegisteredBinding
import com.project.rumahsakitrujukancovid_19.databinding.LayoutLoadingBinding

const val PATH_USER = "user"
const val USER_NORMAL = "user_normal"
const val HOSPITAL_ADMIN = "hospital admin"
const val PATH_HOSPITAL = "hospital"
const val PATH_FAVORITE = "favorite"
const val USER_KEY = "user_key"

const val EXTRA_DATA_FOR_DETAIL = "extra_data_for_detail"

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun showAlertDialog(context: Context, message: String, listenerNext: () -> Unit):  AlertDialog{
    val binding = LayoutAlertSignUpBinding.inflate(LayoutInflater.from(context), null, false)
    val dialog = MaterialAlertDialogBuilder(context)
        .setView(binding.root)
        .setCancelable(false)
        .create()

    binding.apply {
        tvRegisterAs.text = message
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnNext.setOnClickListener {
            dialog.dismiss()
            listenerNext()
        }
    }

    return dialog
}

fun showAlertDialogAlreadyRegistered(context: Context, desc: String): AlertDialog{
    val binding = LayoutAlreadyRegisteredBinding.inflate(LayoutInflater.from(context), null, false)
    val dialog = MaterialAlertDialogBuilder(context)
        .setView(binding.root)
        .setCancelable(false)
        .create()

    binding.apply {
        tvDesc.text = desc
        btnOk.setOnClickListener { dialog.dismiss() }
    }

    return dialog
}

fun showAlertLoading(context: Context): AlertDialog{
    val binding = LayoutLoadingBinding.inflate(LayoutInflater.from(context), null, false)

    return MaterialAlertDialogBuilder(context, R.style.CustomDialogLoading)
        .setView(binding.root)
        .setCancelable(false)
        .create()
}


fun View.showSnackBar(message: String){
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun Context.showToast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun hideSoftKeyboard(context: Context, view: View){
    (context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(view.windowToken, 0)
}

fun ImageView.loadImage(url: String) {
    Glide.with(this.context)
        .load(Uri.parse(url))
        .into(this)
}