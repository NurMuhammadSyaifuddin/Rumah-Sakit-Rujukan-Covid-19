package com.project.rumahsakitrujukancovid_19.ui.signup

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.rxbinding2.widget.RxTextView
import com.project.core.data.source.Resource
import com.project.rumahsakitrujukancovid_19.BuildConfig
import com.project.rumahsakitrujukancovid_19.R
import com.project.rumahsakitrujukancovid_19.databinding.*
import com.project.rumahsakitrujukancovid_19.ui.admin.HospitalAdminLocationActivity
import com.project.rumahsakitrujukancovid_19.ui.login.LoginActivity
import com.project.rumahsakitrujukancovid_19.utils.*
import io.reactivex.Observable
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

@SuppressLint("CheckResult")
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val customDialog by lazy { CustomDialog() }

    private val viewModel: SignUpViewModel by viewModel()

    private lateinit var loading: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init
        loading = showAlertLoading(this@SignUpActivity)

        processedAccount()

    }

    private fun processedAccount() {
        binding.apply {
            val nameStream = RxTextView.textChanges(edtName)
                .skipInitialValue()
                .map { name ->
                    name.isBlank()
                }

            nameStream.subscribe {
                showNameExistAlert(it)
            }

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

            val confirmPasswordStream = Observable.merge(
                RxTextView.textChanges(edtConfirmPassword)
                    .skipInitialValue()
                    .map { confirmPassword ->
                        confirmPassword.length < 8
                    },
                RxTextView.textChanges(edtConfirmPassword)
                    .skipInitialValue()
                    .map { confirmPassword ->
                        confirmPassword.toString() != edtPassword.text.toString()
                    }
            )

            confirmPasswordStream.subscribe {
                showConfirmPasswordAlert(it)
            }

            val invalidFieldStream = Observable.combineLatest(
                nameStream,
                emailStream,
                passwordStream,
                confirmPasswordStream
            ) { nammeInvalid, emailInvalid, passwordInvalid, confirmPasswordInvalid ->
                !nammeInvalid && !emailInvalid && !passwordInvalid && !confirmPasswordInvalid
            }

            invalidFieldStream.subscribe {
                showButtonSignUp(it)
            }

            btnLogin.setOnClickListener {
                Intent(this@SignUpActivity, LoginActivity::class.java).also { intent ->
                    startActivity(intent)
                }
            }

            btnSignUp.setOnClickListener {
                hideSoftKeyboard(this@SignUpActivity, binding.root)
                when (rgUserAs.checkedRadioButtonId) {
                    rbProspectivePatient.id ->
                        showAlertDialog(
                            this@SignUpActivity,
                            rbProspectivePatient.text.toString(),
                        ) {
                            loading.show()
                            try {
                                auth.createUserWithEmailAndPassword(
                                    edtEmail.text.toString(),
                                    edtPassword.text.toString()
                                ).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        val uid = auth.currentUser?.uid.toString()
                                        val name = edtName.text?.trim().toString()
                                        val photoUrl = "${BuildConfig.BASE_URL_IMAGE_PROFILE}$name"
                                        val email = edtEmail.text?.trim().toString()
                                        val user =
                                            enterUser(uid, name, email, photoUrl, USER_NORMAL)

                                        viewModel.setCollectionUser(db, uid).set(user)
                                        viewModel.saveLevelUser(USER_NORMAL)

                                        sendVerificationEmail()

                                        customDialog.showSuccessCreateAccount(this@SignUpActivity) {
                                            Intent(
                                                this@SignUpActivity,
                                                LoginActivity::class.java
                                            ).also { intent ->
                                                startActivity(intent)
                                                finishAffinity()
                                            }
                                        }
                                        loading.dismiss()
                                    }
                                }.addOnFailureListener {
                                    Toast.makeText(
                                        this@SignUpActivity,
                                        it.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loading.dismiss()
                                }
                            } catch (e: Exception) {
                                Timber.e(e.message.toString())
                            }
                        }.show()

                    rbHospitalAdmin.id ->
                        showAlertDialog(this@SignUpActivity, rbHospitalAdmin.text.toString()) {

                            viewModel.getHospital().observe(this@SignUpActivity) { hospital ->
                                if (hospital != null) {
                                    when (hospital) {
                                        is Resource.Loading -> loading.show()
                                        is Resource.Success -> {

                                            val uid = auth.currentUser?.uid.toString()
                                            val name = edtName.text?.trim().toString()
                                            val photoUrl =
                                                "${BuildConfig.BASE_URL_IMAGE_PROFILE}$name"
                                            val email = edtEmail.text?.trim().toString()
                                            val password = edtPassword.text?.trim().toString()

                                            val emailAdminIsReady =
                                                hospital.data?.asSequence()?.map { it.emailAdmin }
                                                    ?.contains(email)

                                            val hospitalAdminIsWork =
                                                hospital.data?.asSequence()?.filter { it.emailAdmin == email }
                                                    ?.take(1)
                                                    ?.toList()

                                            if (emailAdminIsReady as Boolean) {
                                                val admin =
                                                    enterUser(
                                                        uid,
                                                        name,
                                                        email,
                                                        photoUrl,
                                                        HOSPITAL_ADMIN
                                                    )

                                                Intent(
                                                    this@SignUpActivity,
                                                    HospitalAdminLocationActivity::class.java
                                                ).also { intent ->
                                                    intent.putExtra(
                                                        HospitalAdminLocationActivity.EXTRA_DATA,
                                                        admin
                                                    )
                                                    intent.putExtra(
                                                        HospitalAdminLocationActivity.EXTRA_EMAIL,
                                                        email
                                                    )
                                                    intent.putExtra(
                                                        HospitalAdminLocationActivity.EXTRA_PASSWORD,
                                                        password
                                                    )
                                                    intent.putExtra(
                                                        HospitalAdminLocationActivity.EXTRA_HOSPITAL,
                                                        hospitalAdminIsWork?.get(0)?.name
                                                    )

                                                    startActivity(intent)
                                                    loading.dismiss()
                                                }
                                            } else {
                                                this@SignUpActivity.showToast(getString(R.string.email_admin_not_valid))
                                                loading.dismiss()
                                            }
                                        }
                                        is Resource.Error -> {
                                            this@SignUpActivity.showToast(hospital.message.toString())
                                            loading.dismiss()
                                        }
                                    }
                                }
                            }

                        }.show()
                }
            }

        }
    }

    private fun enterUser(
        uid: String,
        name: String,
        email: String,
        photoUrl: String,
        type: String
    ) =
        hashMapOf(
            "id" to uid,
            "name" to name,
            "email" to email,
            "photoUrl" to photoUrl,
            "status" to type
        )

    private fun sendVerificationEmail() {
        auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { auth.signOut() }
    }

    private fun showButtonSignUp(isValid: Boolean) {
        if (isValid) binding.btnSignUp.enable() else binding.btnLogin.disable()
    }

    private fun showConfirmPasswordAlert(isNotValid: Boolean) {
        binding.textInputConfirmPassword.error =
            if (isNotValid) resources.getString(R.string.password_is_not_same) else null
    }

    private fun showPasswordMinimalAlert(isNotValid: Boolean) {
        binding.textInputPassword.error =
            if (isNotValid) resources.getString(R.string.password_length_less_than_8) else null
    }

    private fun showEmailExistAlert(isNotValid: Boolean) {
        binding.textInputEmail.error =
            if (isNotValid) resources.getString(R.string.email_not_valid) else null
    }

    private fun showNameExistAlert(isNotValid: Boolean) {
        binding.textInputName.error =
            if (isNotValid) resources.getString(R.string.name_cannot_blank) else null
    }

}