package com.project.hospital_admin.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.domain.model.Registration
import com.project.core.domain.model.User
import com.project.core.utils.EXTRA_DATA_FOR_REGISTRATION
import com.project.core.utils.WAIT
import com.project.hospital_admin.databinding.FragmentHomeBinding
import com.project.hospital_admin.di.registrationModule
import com.project.rumahsakitrujukancovid_19.notification.ReceiveRegistrationService
import com.project.hospital_admin.ui.registration.CheckingRegistrationActivity
import com.project.rumahsakitrujukancovid_19.utils.gone
import com.project.rumahsakitrujukancovid_19.utils.visible
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var binding: FragmentHomeBinding

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    private lateinit var alarmReceiver: ReceiveRegistrationService

    private lateinit var activityAdminAdapter: ActivityAdminAdapter

    private val viewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        if (_binding != null) {
            binding = _binding as FragmentHomeBinding
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadKoinModules(registrationModule)

        // init
        alarmReceiver = ReceiveRegistrationService()
        activityAdminAdapter = ActivityAdminAdapter().apply {
            onClick {

                if (it.statusRegistration == WAIT) {
                    Intent(
                        activity,
                        CheckingRegistrationActivity::class.java
                    ).also { intent ->
                        intent.putExtra(CheckingRegistrationActivity.EXTRA_DATA, it)
                        startActivity(intent)
                    }
                } else {
                    Intent(
                        activity,
                        Class.forName("com.project.user.ui.registration.DetailRegistrationActivity")
                    ).also { intent ->
                        intent.putExtra(EXTRA_DATA_FOR_REGISTRATION, it)
                        startActivity(intent)
                    }
                }
            }
        }

        alarmReceiver.setUpRepeatingAlarm(activity?.applicationContext as Context)

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
                                    .sortedByDescending {
                                        it?.registrationDate
                                    }

                                if (dataRegister.isNullOrEmpty()) {
                                    showEmptyActivity(true)
                                } else {
                                    showEmptyActivity(false)
                                    activityAdminAdapter.registration =
                                        dataRegister as MutableList<Registration>
                                    rvActivities.adapter = activityAdminAdapter
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

    override fun onDestroyView() {
        super.onDestroyView()
        unloadKoinModules(registrationModule)
        _binding = null
        binding.rvActivities.adapter = null
    }
}