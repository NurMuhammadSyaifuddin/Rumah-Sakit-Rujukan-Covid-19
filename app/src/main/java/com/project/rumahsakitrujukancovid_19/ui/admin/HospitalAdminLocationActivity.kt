package com.project.rumahsakitrujukancovid_19.ui.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.data.source.Resource
import com.project.rumahsakitrujukancovid_19.databinding.ActivityHospitalAdminLocationBinding
import com.project.rumahsakitrujukancovid_19.ui.login.LoginActivity
import com.project.rumahsakitrujukancovid_19.utils.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.lang.Exception

class HospitalAdminLocationActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val customDialog by lazy { CustomDialog() }

    private lateinit var binding: ActivityHospitalAdminLocationBinding

    private lateinit var loading: AlertDialog

    private val viewModel: HospitalAdminLocationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalAdminLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init
        loading = showAlertLoading(this)

        setItemHospital()
        signInAdmin()
    }

    private fun signInAdmin() {

        binding.apply {

            intent?.let {
                val admin = it.extras?.getSerializable(EXTRA_DATA) as HashMap<String, String>
                val email = it.extras?.getString(EXTRA_EMAIL)
                val password = it.extras?.getString(EXTRA_PASSWORD)

                btnNext.setOnClickListener {
                    processedAccount(admin, email.toString(), password.toString())
                }
            }
        }
    }

    private fun processedAccount(admin: HashMap<String, String>, email: String, password: String) {
        binding.apply {
            hideSoftKeyboard(this@HospitalAdminLocationActivity, binding.root)
            loading.show()
            try {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {

                            admin["id"] = auth.currentUser?.uid.toString()

                            viewModel.setCollectionUser(db, admin["id"].toString())
                                .set(admin)
                            viewModel.saveLevelUser(HOSPITAL_ADMIN)

                            sendVerificationEmail()

                            customDialog.showSuccessCreateAccount(this@HospitalAdminLocationActivity) {
                                Intent(
                                    this@HospitalAdminLocationActivity,
                                    LoginActivity::class.java
                                ).also { intent ->
                                    startActivity(intent)
                                    finishAffinity()
                                }
                            }

                            loading.dismiss()

                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this@HospitalAdminLocationActivity,
                            it.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        loading.dismiss()
                    }


            } catch (e: Exception) {
                Timber.e(e.message.toString())
            }
        }
    }

    private fun setItemHospital() {
        binding.apply {
            viewModel.getHospitalName()
                .observe(this@HospitalAdminLocationActivity) { hospital ->
                    if (hospital != null) {
                        when (hospital) {
                            is Resource.Loading ->
                                loading.show()
                            is Resource.Success -> {
                                getHospitalName()
                                loading.dismiss()
                            }
                            is Resource.Error -> {
                                showToast(hospital.message.toString())
                                loading.dismiss()
                            }
                        }
                    }
                }

        }
    }

    private fun getHospitalName() {
        binding.apply {

            val hospitalAdminIsWork = intent?.extras?.getString(EXTRA_HOSPITAL)

            edtHospital.setText(hospitalAdminIsWork)

            if (!edtHospital.text.isNullOrBlank()) btnNext.enable()
            else btnNext.disable()

        }
    }

    private fun sendVerificationEmail() {
        auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { auth.signOut() }
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_PASSWORD = "extra_password"
        const val EXTRA_HOSPITAL = "extra_hospital"
    }
}