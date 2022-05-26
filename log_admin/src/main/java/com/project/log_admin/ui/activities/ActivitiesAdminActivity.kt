package com.project.log_admin.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.domain.model.Registration
import com.project.core.domain.model.User
import com.project.core.utils.EXTRA_DATA_FOR_REGISTRATION
import com.project.core.utils.wait
import com.project.log_admin.databinding.ActivityActvitiesAdminBinding
import com.project.log_admin.ui.log.LogViewModel
import com.project.log_admin.ui.registration.CheckingRegistrationActivity
import com.project.rumahsakitrujukancovid_19.utils.gone
import com.project.rumahsakitrujukancovid_19.utils.visible
import org.koin.android.viewmodel.ext.android.viewModel

class ActivitiesAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActvitiesAdminBinding

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    private lateinit var adapter: ActivitiesAdminAdapter



    private val viewModel: LogViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActvitiesAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ActivitiesAdminAdapter().apply {
            onClick {
                if (it.statusRegistration == this@ActivitiesAdminActivity.wait()) {
                    Intent(
                        this@ActivitiesAdminActivity,
                        CheckingRegistrationActivity::class.java
                    ).also { intent ->
                        intent.putExtra(CheckingRegistrationActivity.EXTRA_DATA, it)
                        startActivity(intent)
                    }
                } else {
                    Intent(
                        this@ActivitiesAdminActivity,
                        Class.forName("com.project.user.ui.registration.DetailRegistrationActivity")
                    ).also { intent ->
                        intent.putExtra(EXTRA_DATA_FOR_REGISTRATION, it)
                        startActivity(intent)
                    }
                }
            }
        }

        getActivitiesAdmin()

        onAction()
    }

    private fun onAction() {
        binding.apply {
            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = true
                getActivitiesAdmin()
            }
        }
    }

    private fun getActivitiesAdmin() {
        binding.apply {
            viewModel.collectionUser(db)
                .get()
                .addOnSuccessListener { query ->
                    val data = query.documents.asSequence()
                        .map {
                            it.toObject(User::class.java)
                        }
                        .filter {
                            it?.id == auth.currentUser?.uid.toString()
                        }
                        .take(1)
                        .toList()

                    if (data.isNotEmpty()) {
                        viewModel.collectionRegistration(db, data[0]?.email.toString())
                            .get()
                            .addOnSuccessListener { value ->
                                val dataRegister = value.documents
                                    .map {
                                        it.toObject(Registration::class.java)
                                    }
                                    .filter {
                                        val date = intent?.extras?.getString(EXTRA_DATE_FOR_REGISTRATION)
                                        tvDate.text = date.toString()

                                        it?.registrationDate?.split(" ")?.toTypedArray()?.get(0) == date
                                    }
                                    .sortedByDescending {
                                        it?.registrationDate
                                    }

                                if (dataRegister.isNullOrEmpty()) {
                                    showEmptyActivity(true)
                                } else {
                                    showEmptyActivity(false)
                                    adapter.registrations =
                                        dataRegister as MutableList<Registration>
                                    rvActivities.adapter = adapter
                                    rvActivities.setHasFixedSize(true)
                                }

                            }
                    }
                }
        }
    }

    private fun showEmptyActivity(state: Boolean) {
        binding.apply {
            if (state) {
                rvActivities.gone()
                imgEmptyState.visible()
                titleEmptyState.visible()
                descEmptyState.visible()
            } else {
                rvActivities.visible()
                imgEmptyState.gone()
                titleEmptyState.gone()
                descEmptyState.gone()
            }
            swipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.rvActivities.adapter = null
    }

    companion object{
        const val EXTRA_DATE_FOR_REGISTRATION = "extra_date"
    }
}