package com.project.rumahsakitrujukancovid_19.ui.forgot

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView
import com.project.rumahsakitrujukancovid_19.R
import com.project.rumahsakitrujukancovid_19.databinding.ActivityForgotBinding
import com.project.rumahsakitrujukancovid_19.ui.login.LoginActivity
import com.project.rumahsakitrujukancovid_19.utils.*

class ForgotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotBinding

    private lateinit var loading: AlertDialog
    private lateinit var alertDialogSuccess: CustomDialog

    private val auth by lazy { FirebaseAuth.getInstance() }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init
        loading = showAlertLoading(this)
        alertDialogSuccess = CustomDialog()
        
        onAction()

    }

    @SuppressLint("CheckResult")
    private fun onAction() {
        binding.apply {
            btnLogin.setOnClickListener {
                Intent(this@ForgotActivity, LoginActivity::class.java).also { intent ->
                    startActivity(intent)
                }
            }

            val emailStream = RxTextView.textChanges(edtEmail)
                .skipInitialValue()
                .map { email ->
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                }

            emailStream.subscribe {
                showExistAlertEmail(it)
            }

            btnForgotPassword.setOnClickListener {
                val email = edtEmail.text?.trim().toString()
                hideSoftKeyboard(this@ForgotActivity, binding.root)
                loading.show()

                forgotPasswordToServer(email)

            }
        }
    }

    private fun forgotPasswordToServer(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                loading.dismiss()

                val desc = getString(R.string.check_your_email_and_change_password)

                alertDialogSuccess.showSuccessCreateAccount(this, desc){
                    Intent(this, LoginActivity::class.java).also { intent ->
                        startActivity(intent)
                        finishAffinity()
                    }
                }
            }
            .addOnFailureListener {
                showToast(it.message.toString())
                loading.dismiss()
            }
    }

    private fun showExistAlertEmail(isNotValid: Boolean) {
        binding.apply {
            if (isNotValid){
                textInputEmail.error = getString(R.string.email_not_valid)
            }else{
                textInputEmail.error = null
                btnForgotPassword.enable()
            }
        }
    }
}