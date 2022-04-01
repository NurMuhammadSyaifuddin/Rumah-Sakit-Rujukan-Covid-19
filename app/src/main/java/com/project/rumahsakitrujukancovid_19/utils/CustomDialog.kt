package com.project.rumahsakitrujukancovid_19.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import com.project.rumahsakitrujukancovid_19.R
import com.project.rumahsakitrujukancovid_19.databinding.DialogSuccessCreateAccountBinding

class CustomDialog {

    private var dialog: Dialog? = null

    private fun isShowing(): Boolean = dialog?.isShowing ?: false

    private fun showDialog(
        context: Context,
        view: View
    ){
        if (isShowing()) return
        dialog = Dialog(context, R.style.AlertDialogTheme)
        dialog?.apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCanceledOnTouchOutside(false)
            setContentView(view)
            show()
        }
    }

    fun showSuccessCreateAccount(context: Context, listener: () -> Unit){
        val binding = DialogSuccessCreateAccountBinding.inflate(LayoutInflater.from(context), null, false)
        showDialog(context, binding.root)
        binding.btnNext.setOnClickListener { listener() }
    }

}