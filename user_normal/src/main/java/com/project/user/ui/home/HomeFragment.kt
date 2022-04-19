package com.project.user.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.project.core.data.source.Resource
import com.project.core.domain.model.Hospital
import com.project.core.domain.model.User
import com.project.core.ui.HospitalAdapter
import com.project.core.utils.loadImage
import com.project.rumahsakitrujukancovid_19.notification.ReceiveResultCheckingActivityService
import com.project.rumahsakitrujukancovid_19.utils.EXTRA_DATA_FOR_DETAIL
import com.project.user.R
import com.project.rumahsakitrujukancovid_19.utils.gone
import com.project.rumahsakitrujukancovid_19.utils.visible
import com.project.user.databinding.FragmentHomeBinding
import com.project.user.di.homeModule
import com.project.user.ui.detail.DetailActivity
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var binding: FragmentHomeBinding

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val user by lazy { auth.currentUser }
    private val db by lazy { Firebase.firestore }
    private val ref by lazy { FirebaseStorage.getInstance().reference }
    private val viewModel: HomeViewModel by viewModel()

    private lateinit var alarmReceiver: ReceiveResultCheckingActivityService

    private lateinit var hospitalAdapter: HospitalAdapter

    private lateinit var database: DatabaseReference

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
        loadKoinModules(homeModule)

        handleBackPressed(requireActivity())

        database = FirebaseDatabase.getInstance().getReference("hospitals")

        // init
        alarmReceiver = ReceiveResultCheckingActivityService()
        hospitalAdapter = HospitalAdapter().apply {
            onClick {
                Intent(activity, DetailActivity::class.java).also { intent ->
                    intent.putExtra(EXTRA_DATA_FOR_DETAIL, it)
                    startActivity(intent)
                }
            }
        }

        alarmReceiver.setUpRepeatingAlarm(activity?.applicationContext as Context)

        if (activity != null) {
            getHospital()
            getUser()
            getSearchHospital()

        }

        onAction()
    }

    private fun onAction() {
        binding.apply {
            imgRefresh.setOnClickListener {
                getHospital()
                getUser()
                getDataSearch()
            }
        }
    }

    private fun handleBackPressed(activiy: FragmentActivity) {
        activiy.onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val textSearch = binding.searchViewHospital.query.toString()
            if (textSearch.isBlank()) {
                this.isEnabled = false
                activiy.onBackPressed()
            } else {
                binding.searchViewHospital.setQuery("", false)
                getDataSearch()
            }
        }
    }

    private fun getSearchHospital() {
        binding.apply {

            val closeButton =
                searchViewHospital.findViewById<View>(androidx.appcompat.R.id.search_close_btn)
            closeButton.setOnClickListener {
                searchViewHospital.setQuery("", false)
                getDataSearch()
            }

            searchViewHospital.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean = false

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (!newText.isNullOrBlank()) {
                        getDataSearch(newText)
                    } else {
                        getDataSearch()
                    }

                    return true
                }

            })
        }
    }

    private fun getDataSearch(text: String = "") {
        binding.apply {

            viewModel.getSearchHospital(text).observe(viewLifecycleOwner) {

                if (it.isNotEmpty()) {
                    hospitalAdapter.listHospital = it as MutableList<Hospital>
                    setUpAdapter()

                    getViewSearch(true)

                } else {
                    if (text.isNotBlank()) {
                        tvEmptySearchHospital.text = getString(R.string.empty_search_hospital, text)
                        getViewSearch(false)
                    } else {
                        getViewSearch(true)
                    }
                }

            }
        }
    }

    private fun getViewSearch(state: Boolean) {
        binding.apply {
            if (state) {
                tvEmptySearchHospital.gone()
                rvHospitals.visible()
            } else {
                tvEmptySearchHospital.visible()
                rvHospitals.gone()
            }
        }

    }

    private fun getUser() {
        binding.apply {
            viewModel.getCollectionUser(db, user?.uid.toString())
                .get()
                .addOnCompleteListener { query ->
                    val data = query.result?.toObject(User::class.java)

                    viewModel.storageReference(ref, user?.uid.toString())
                        .downloadUrl
                        .addOnCompleteListener {
                            if (it.isSuccessful) imgProfile.loadImage(it.result.toString())
                            else imgProfile.loadImage(data?.photoUrl.toString())
                        }
                        .addOnFailureListener {
                            imgProfile.loadImage(data?.photoUrl.toString())
                        }

                    tvSayHelloUser.text =
                        resources.getString(R.string.say_hello_to_user, data?.name.toString())
                }
        }
    }

    private fun getHospital() {
        viewModel.getHospital().observe(viewLifecycleOwner) { hospital ->
            if (hospital != null) {
                when (hospital) {
                    is Resource.Loading -> {
                        binding.loading.visible()
                        binding.rvHospitals.gone()
                    }
                    is Resource.Success -> {
                        binding.loading.gone()
                        binding.rvHospitals.visible()
                        hospitalAdapter.listHospital = hospital.data as MutableList<Hospital>
                    }
                    is Resource.Error -> {
                        binding.loading.gone()
                        binding.rvHospitals.visible()
                        Toast.makeText(activity, hospital.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        setUpAdapter()
    }

    private fun setUpAdapter() {
        binding.apply {
            rvHospitals.setHasFixedSize(true)
            rvHospitals.adapter = hospitalAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unloadKoinModules(homeModule)
        binding.rvHospitals.adapter = null
        _binding = null
    }
}