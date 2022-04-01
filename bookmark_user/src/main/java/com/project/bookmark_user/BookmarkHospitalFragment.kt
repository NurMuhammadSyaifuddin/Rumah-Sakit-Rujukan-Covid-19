package com.project.bookmark_user

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.bookmark_user.databinding.FragmentBookmarkHospitalBinding
import com.project.bookmark_user.di.bookmarkModule
import com.project.core.domain.model.Hospital
import com.project.core.ui.HospitalAdapter
import com.project.rumahsakitrujukancovid_19.utils.EXTRA_DATA_FOR_DETAIL
import com.project.rumahsakitrujukancovid_19.utils.gone
import com.project.rumahsakitrujukancovid_19.utils.visible
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

class BookmarkHospitalFragment : Fragment() {

    private var _binding: FragmentBookmarkHospitalBinding? = null
    private lateinit var binding: FragmentBookmarkHospitalBinding

    private lateinit var hospitalAdapter: HospitalAdapter

    private val db by lazy { Firebase.firestore }
    private val user by lazy { FirebaseAuth.getInstance().currentUser }

    private val viewModel: BookmarkHospitalViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBookmarkHospitalBinding.inflate(inflater, container, false)
        if (_binding != null){
            binding = _binding as FragmentBookmarkHospitalBinding
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadKoinModules(bookmarkModule)

        // init
        hospitalAdapter = HospitalAdapter().apply {
            onClick {
                Intent(
                    activity,
                    Class.forName("com.project.user.ui.detail.DetailActivity")
                ).also { intent ->
                    intent.putExtra(EXTRA_DATA_FOR_DETAIL, it)
                    startActivity(intent)
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        if (_binding != null) {
            getHospitalBookmark()
        }
    }

    private fun getHospitalBookmark() {
        viewModel.getCollectionBookmarkHospital(db, user?.uid.toString())
            .get()
            .addOnSuccessListener { query ->
                val data = query.documents.map { snapshot ->
                    snapshot.toObject(Hospital::class.java)
                }

                if (data.isNullOrEmpty()) {
                    showEmptyBookmark(true)
                } else {
                    showEmptyBookmark(false)
                    hospitalAdapter.listHospital = data as MutableList<Hospital>
                }
            }

        with(binding.rvBookmark) {
            setHasFixedSize(true)
            adapter = hospitalAdapter
        }
    }

    private fun showEmptyBookmark(state: Boolean) {
        binding.apply {
            if (state) {
                rvBookmark.gone()
                imgEmptyState.visible()
                titleEmptyState.visible()
                descEmptyState.visible()
            } else {
                rvBookmark.visible()
                imgEmptyState.gone()
                titleEmptyState.gone()
                descEmptyState.gone()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unloadKoinModules(bookmarkModule)
        binding.rvBookmark.adapter = null
        _binding = null
    }
}