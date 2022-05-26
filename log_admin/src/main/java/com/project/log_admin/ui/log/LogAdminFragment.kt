package com.project.log_admin.ui.log

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.domain.model.Log
import com.project.core.domain.model.Registration
import com.project.core.domain.model.User
import com.project.core.utils.accepted
import com.project.core.utils.rejected
import com.project.core.utils.wait
import com.project.log_admin.databinding.FragmentLogAdminBinding
import com.project.log_admin.di.logModule
import com.project.log_admin.ui.activities.ActivitiesAdminActivity
import com.project.rumahsakitrujukancovid_19.utils.gone
import com.project.rumahsakitrujukancovid_19.utils.visible
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

class LogAdminFragment : Fragment() {

    private var _binding: FragmentLogAdminBinding? = null
    private lateinit var binding: FragmentLogAdminBinding

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    private lateinit var adapter: LogAdapter

    private val viewModel: LogViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLogAdminBinding.inflate(inflater, container, false)
        if (_binding != null) {
            binding = _binding as FragmentLogAdminBinding
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadKoinModules(logModule)

        adapter = LogAdapter().apply {
            onClick { date ->
                Intent(activity, ActivitiesAdminActivity::class.java).also { intent ->
                    intent.putExtra(ActivitiesAdminActivity.EXTRA_DATE_FOR_REGISTRATION, date)
                    startActivity(intent)
                }
            }
        }

        getLogAdmin()

        onAction()
    }

    private fun onAction() {
        binding.apply {
            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = true
                getLogAdmin()
            }
        }
    }

    private fun getLogAdmin() {
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
                                    .groupBy {
                                        it?.registrationDate?.split(" ")?.toTypedArray()?.get(0)
                                    }

                                val registrationForLog = mutableListOf<List<Registration?>>()
                                val detailLog = mutableListOf<Log>()

                                for (i in dataRegister.entries) {
                                    val dataValue = i.value
                                    registrationForLog.add(dataValue)

                                    detailLog.add(
                                        Log(
                                            dataValue.take(1)[0]?.registrationDate?.split(" ")
                                                ?.toTypedArray()!![0],
                                            dataValue.filter { it?.statusRegistration == activity?.wait() }
                                                .count(),
                                            dataValue.filter { it?.statusRegistration == activity?.rejected() }
                                                .count(),
                                            dataValue.filter { it?.statusRegistration == activity?.accepted() }
                                                .count()
                                        )
                                    )
                                }

                                if (registrationForLog.isNotEmpty()) {
                                    adapter.registration =
                                        registrationForLog as MutableList<List<Registration>>
                                    adapter.log = detailLog

                                    rvLog.adapter = adapter
                                    rvLog.setHasFixedSize(true)

                                    showEmptyLog(false)
                                } else {
                                    showEmptyLog(true)
                                }

                            }
                    }
                }
        }
    }

    private fun showEmptyLog(state: Boolean) {
        binding.apply {
            if (state) {
                rvLog.gone()
                imgEmptyState.visible()
                titleEmptyState.visible()
                descEmptyState.visible()
            } else {
                rvLog.visible()
                imgEmptyState.gone()
                titleEmptyState.gone()
                descEmptyState.gone()
            }
            swipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unloadKoinModules(logModule)
        _binding = null
        binding.rvLog.adapter = null
    }
}