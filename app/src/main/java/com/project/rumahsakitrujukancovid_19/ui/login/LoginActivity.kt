package com.project.rumahsakitrujukancovid_19.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding2.widget.RxTextView
import com.project.core.domain.model.User
import com.project.rumahsakitrujukancovid_19.R
import com.project.rumahsakitrujukancovid_19.databinding.ActivityLoginBinding
import com.project.rumahsakitrujukancovid_19.ui.signup.SignUpActivity
import com.project.rumahsakitrujukancovid_19.utils.*
import io.reactivex.Observable
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.lang.Exception

@SuppressLint("CheckResult")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { Firebase.firestore }

    private val viewModel: LoginViewModel by viewModel()

    private lateinit var loading: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init
        loading = showAlertLoading(this)

        processedLogin()

        onAction()
    }

    private fun processedLogin() {
        binding.apply {
            val emailStream = RxTextView.textChanges(edtEmail)
                .skipInitialValue()
                .map { email ->
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                }

            emailStream.subscribe {
                showEmailExistAlert(it)
            }

            val passwordStream = RxTextView.textChanges(edtPassword)
                .skipInitialValue()
                .map { password ->
                    password.length < 8
                }

            passwordStream.subscribe {
                showPasswordMinimalAlert(it)
            }

            val invalidFieldStream = Observable.combineLatest(
                emailStream,
                passwordStream
            ) { emailInvalid, passwordInvalid->
                !emailInvalid && !passwordInvalid
            }

            invalidFieldStream.subscribe {
                showButtonLogin(it)
            }

        }
    }

    private fun showButtonLogin(isValid: Boolean) {
        if (isValid) binding.btnLogin.enable() else binding.btnLogin.disable()
    }

    private fun onAction() {
        binding.apply {
            btnRegister.setOnClickListener {
                Intent(this@LoginActivity, SignUpActivity::class.java).also {
                    startActivity(it)
                }
            }

            btnLogin.setOnClickListener { signInWithEmail() }
        }
    }

    private fun signInWithEmail() {
        binding.apply {
            loading.show()
            hideSoftKeyboard(this@LoginActivity, binding.root)
            try {
                auth.signInWithEmailAndPassword(
                    edtEmail.text.toString(),
                    edtPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful){
                        if (auth.currentUser?.isEmailVerified as Boolean){
                            destinationUser()
                        }else{
                            Toast.makeText(this@LoginActivity, getString(R.string.email_not_verify), Toast.LENGTH_SHORT).show()
                            auth.signOut()
                        }
                        loading.dismiss()
                    }else{
                        Toast.makeText(this@LoginActivity, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                        Timber.e(it.exception?.message.toString())
                        loading.dismiss()
                    }
                }
            }catch (e: Exception){
                Timber.e(e.message.toString())
                e.printStackTrace()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            viewModel.getLevelUser().observe(this){
                when(it){
                    USER_NORMAL -> moveToUserNormalModule()
                    HOSPITAL_ADMIN -> {}
                }
            }
        }
    }

    private fun destinationUser(){
        viewModel.getCollectionUser(db)
            .get()
            .addOnSuccessListener { query ->
                val data = query.documents
                    .asSequence()
                    .filter { it.id == auth.currentUser?.uid }
                    .map { snapshot ->
                        snapshot.toObject(User::class.java)
                    }
                    .take(1)

                when (data.toList()[0]?.status){
                    USER_NORMAL -> {
                        moveToUserNormalModule()
                    }
                    HOSPITAL_ADMIN -> {
                        moveToHospitalAdmin()
                    }
                }
            }
    }

    private fun moveToHospitalAdmin() {
        Intent(this, Class.forName("com.project.hospital_admin.MainHospitalAdminActivity")).also {
            startActivity(it)
            finishAffinity()
        }
    }

    private fun moveToUserNormalModule(){
        Intent(this, Class.forName("com.project.user.MainUserActivity")).also {
            startActivity(it)
            finishAffinity()
        }
    }

    private fun showPasswordMinimalAlert(isNotValid: Boolean) {
        binding.textInputPassword.error =
            if (isNotValid) resources.getString(R.string.password_length_less_than_8) else null
    }

    private fun showEmailExistAlert(isNotValid: Boolean) {
        binding.textInputEmail.error =
            if (isNotValid) resources.getString(R.string.email_not_valid) else null
    }
}