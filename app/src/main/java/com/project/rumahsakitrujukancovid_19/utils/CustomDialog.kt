package com.project.rumahsakitrujukancovid_19.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.project.rumahsakitrujukancovid_19.R
import com.project.rumahsakitrujukancovid_19.databinding.DialogSuccessCreateAccountBinding

class CustomDialog {

    private var dialog: Dialog? = null

    fun showSuccessCreateAccount(context: Context, desc: String = "", listener: () -> Unit) {
        val binding =
            DialogSuccessCreateAccountBinding.inflate(LayoutInflater.from(context), null, false)

        if (desc.isNotBlank()) {
            binding.dialogDescription.text = desc
        }

        dialog = Dialog(context, R.style.AlertDialogTheme)
        dialog?.apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCanceledOnTouchOutside(false)
            setContentView(binding.root)
            show()
        }
        binding.btnNext.setOnClickListener {
            listener()
            dialog!!.dismiss()
        }
    }

}