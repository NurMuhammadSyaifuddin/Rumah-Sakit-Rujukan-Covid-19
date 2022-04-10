package com.project.activity_user

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.activity_user.di.activityUserModule
import com.project.core.domain.model.Registration
import com.project.core.utils.EXTRA_DATA_FOR_REGISTRATION
import com.project.history_user.databinding.FragmentActivitiesUserBinding
import com.project.rumahsakitrujukancovid_19.utils.gone
import com.project.rumahsakitrujukancovid_19.utils.visible
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

class ActivitiesUserFragment : Fragment() {

    private var _binding: FragmentActivitiesUserBinding? = null
    private lateinit var binding: FragmentActivitiesUserBinding

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    private lateinit var adapter: ActivityUserAdapter

    private val viewModel: ActivityUserViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentActivitiesUserBinding.inflate(inflater, container, false)
        if (_binding != null) {
            binding = _binding as FragmentActivitiesUserBinding
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadKoinModules(activityUserModule)

        // init
        adapter = ActivityUserAdapter().apply {
            onClick {
                Intent(
                    activity,
                    Class.forName("com.project.user.ui.registration.DetailRegistrationActivity")
                ).also { intent ->
                    intent.putExtra(EXTRA_DATA_FOR_REGISTRATION, it)
                    startActivity(intent)
                }
            }
        }

        getActivitiesUser()
    }

    private fun getActivitiesUser() {
        binding.apply {
            viewModel.collectionRegistration(db, auth.currentUser?.uid.toString())
                .get()
                .addOnSuccessListener { query ->

                    val dataRegister = query.documents.map {
                        it.toObject(Registration::class.java)
                    }
                        .sortedByDescending {
                            it?.registrationDate
                        }

                    if (dataRegister.isNullOrEmpty()) {
                        showEmptyActivity(true)

                    } else {
                        showEmptyActivity(false)
                        adapter.registration = dataRegister as MutableList<Registration>
                        rvActivities.adapter = adapter
                        rvActivities.setHasFixedSize(true)
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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unloadKoinModules(activityUserModule)
        binding.rvActivities.adapter = null
        _binding = null
    }
}