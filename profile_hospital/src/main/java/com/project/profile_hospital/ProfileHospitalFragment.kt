package com.project.profile_hospital

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.core.data.source.Resource
import com.project.core.domain.model.Hospital
import com.project.core.domain.model.Registration
import com.project.core.domain.model.User
import com.project.profile_hospital.databinding.FragmentProfileHospitalBinding
import com.project.profile_hospital.di.profileHospitalModule
import com.project.profile_hospital.utils.showAlertDialogEditName
import com.project.profile_hospital.utils.showBottomSheetDialogEditImage
import com.project.rumahsakitrujukancovid_19.utils.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import java.io.ByteArrayOutputStream

class ProfileHospitalFragment : Fragment() {

    private var _binding: FragmentProfileHospitalBinding? = null
    private lateinit var binding: FragmentProfileHospitalBinding

    private val viewModel: ProfileHospitalViewModel by viewModel()

    private var idHospital: String? = null

    private val realtimeDatabase by lazy {
        FirebaseDatabase.getInstance().getReference("hospitals")
    }

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val currentUser by lazy { auth.currentUser }
    private val ref by lazy { FirebaseStorage.getInstance().reference }

    private var hospitalAdmin: Hospital? = null

    private val launchCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = result.data
                handleImageResult(task)
            }
        }

    private val launchGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = result.data
                handleImageResultFromGallery(task)
            }
        }

    private fun handleImageResultFromGallery(task: Intent?) {
        val uri = task?.data as Uri

        if (hospitalAdmin != null) {
            viewModel.storageReference(ref, hospitalAdmin?.name.toString())
                .putFile(uri)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        viewModel.storageReference(ref, hospitalAdmin?.name.toString())
                            .downloadUrl
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    realtimeDatabase.child(idHospital.toString()).child(IMAGE_URL)
                                        .setValue(task.result.toString())
                                        .addOnSuccessListener {
                                            (activity as FragmentActivity).showToast(getString(R.string.hospital_image_updated))
                                            getProfileHospital()
                                            editPhotoForRegistration(task.result.toString())
                                        }
                                        .addOnFailureListener { error ->
                                            (activity as FragmentActivity).showToast(error.message.toString())
                                        }
                                }
                            }
                    }
                }
                .addOnFailureListener {
                    (activity as FragmentActivity).showToast(it.message.toString())
                }
        }
    }

    private fun handleImageResult(task: Intent?) {
        val bitmap = task?.extras?.get("data") as Bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        viewModel.storageReference(ref, hospitalAdmin?.name.toString())
            .putBytes(data)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    viewModel.storageReference(ref, hospitalAdmin?.name.toString())
                        .downloadUrl
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                realtimeDatabase.child(idHospital.toString()).child(IMAGE_URL)
                                    .setValue(task.result.toString())
                                    .addOnSuccessListener {
                                        (activity as FragmentActivity).showToast(getString(R.string.hospital_image_updated))
                                        getProfileHospital()
                                        editPhotoForRegistration(task.result.toString())
                                    }
                                    .addOnFailureListener { error ->
                                        (activity as FragmentActivity).showToast(error.message.toString())
                                    }
                            }
                        }
                }
            }
            .addOnFailureListener {
                (activity as FragmentActivity).showToast(it.message.toString())
            }
    }

    private fun editPhotoForRegistration(imageUrl: String) {
        viewModel.collectionUser(db)
            .get()
            .addOnSuccessListener { query ->
                val user = query.documents.asSequence()
                    .map {
                        it.toObject(User::class.java)
                    }
                    .filter {
                        it?.id == auth.currentUser?.uid.toString() && it.status == HOSPITAL_ADMIN
                    }
                    .take(1)
                    .toList()

                if (user.isNotEmpty()) {
                    viewModel.collectionRegistrationAdmin(db, user[0]?.email.toString())
                        .get()
                        .addOnSuccessListener { queryAdmin ->
                            val registrations = queryAdmin.documents
                                .map {
                                    it.toObject(Registration::class.java)
                                }

                            if (registrations.isNotEmpty()) {
                                for (index in registrations.indices) {
                                    updateRegistrationAdmin(
                                        index,
                                        registrations,
                                        user[0]?.email.toString(),
                                        imageUrl
                                    )
                                }
                            }

                        }
                }
            }
    }

    private fun updateRegistrationAdmin(
        index: Int,
        registrations: List<Registration?>,
        email: String,
        imagUrl: String
    ) {
        val data = registrations[index]

        viewModel.collectionRegistrationAdminDocument(db, email, data?.registrationNumber.toString())
            .update("imageUrl", imagUrl)

        viewModel.collectionRegistrationUserDocument(
            db,
            data?.idUser.toString(),
            data?.registrationNumber.toString()
        )
            .update("imageUrl", imagUrl)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileHospitalBinding.inflate(inflater, container, false)
        if (_binding != null) {
            binding = _binding as FragmentProfileHospitalBinding
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadKoinModules(profileHospitalModule)
        super.onViewCreated(view, savedInstanceState)

        getProfileHospital()

        onAction()
    }

    private fun onAction() {
        binding.apply {

            imgEditLocation.setOnClickListener {
                updateHospitalAdmin(
                    ADDRESS_HOSPITAL,
                    tvAddress.text.toString(),
                    getString(R.string.address_changed_successfully),
                    getString(R.string.enter_hosptital_address)
                )
            }

            imgEditWebsiteUrl.setOnClickListener {
                updateHospitalAdmin(
                    WEBSITE_URL,
                    tvWebsiteUrl.text.toString(),
                    getString(R.string.website_url_changed_successfully),
                    getString(R.string.enter_website_url)
                )
            }

            imgEditPhone.setOnClickListener {
                updateHospitalAdmin(
                    PHONE_HOSPITAL,
                    tvTelephone.text.toString(),
                    getString(R.string.phone_changed_successfully),
                    getString(R.string.enter_hospital_phone)
                )
            }

            icCopy.setOnClickListener {
                copyToClipBoard(tvTelephone.text.toString())
                (activity as FragmentActivity).showToast(
                    getString(
                        R.string.phone_is_copied,
                        tvTelephone.text.toString()
                    )
                )
            }

            imgPoster.setOnClickListener {
                getPickerEditImage()
            }
        }
    }

    private fun getPickerEditImage() {
        showBottomSheetDialogEditImage(activity as FragmentActivity,
            {
                openCamera()
            },
            {
                openGallery()
            }
        ).show()
    }

    private fun openGallery() {
        Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).also { intent ->
            intent.resolveActivity((activity as FragmentActivity).packageManager).also {
                launchGallery.launch(intent)
            }
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
            launchCamera.launch(it)
        }
    }

    private fun copyToClipBoard(text: String) {
        val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(getString(R.string.label), text)
        clipboard.setPrimaryClip(clip)
    }

    private fun updateHospitalAdmin(
        field: String,
        hint: String,
        message: String,
        titleDialog: String? = null
    ) {
        binding.apply {
            showAlertDialogEditName(activity as FragmentActivity, hint, titleDialog) { data ->
                realtimeDatabase.child(idHospital.toString()).child(field).setValue(data)
                    .addOnSuccessListener {
                        (activity as FragmentActivity).showToast(message)
                        getProfileHospital()
                    }
                    .addOnFailureListener { error ->
                        (activity as FragmentActivity).showToast(error.message.toString())
                    }
            }.show()
        }
    }

    private fun getProfileHospital() {
        binding.apply {
            viewModel.getHospital().observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Loading -> showLoading(true)
                    is Resource.Success -> {
                        viewModel.collectionUser(db)
                            .get()
                            .addOnCompleteListener { task ->
                                val user = task.result?.asSequence()
                                    ?.map { query ->
                                        query.toObject(User::class.java)
                                    }
                                    ?.filter { user ->
                                        user.id == currentUser?.uid.toString() && user.status == HOSPITAL_ADMIN
                                    }
                                    ?.take(1)
                                    ?.toList()

                                if (user?.isNotEmpty() == true) {
                                    val data = it.data?.asSequence()
                                        ?.filter { hospital ->
                                            hospital.emailAdmin == user[0].email
                                        }
                                        ?.take(1)
                                        ?.toList()

                                    val hospital = data?.get(0)

                                    hospitalAdmin = hospital

                                    imgPoster.loadImage(hospital?.imageUrl.toString())
                                    tvName.text = hospital?.name.toString()
                                    tvAddress.text = hospital?.address.toString()
                                    tvWebsiteUrl.text = hospital?.websiteUrl.toString()
                                    tvTelephone.text = hospital?.phone.toString()

                                    val snapshot = it.snapshot

                                    val id = snapshot?.children?.asSequence()
                                        ?.map { value ->
                                            value.key.toString()
                                        }
                                        ?.filter { dataSnapshot ->
                                            dataSnapshot == it.data?.indexOf(data?.get(0) as Hospital)
                                                .toString()
                                        }
                                        ?.take(1)
                                        ?.toList()


                                    idHospital = id?.get(0)

                                    showLoading(false)
                                }
                            }


                    }
                    is Resource.Error -> {
                        showLoading(false)
                        (activity as FragmentActivity).showToast(it.message.toString())
                    }
                }
            }
        }
    }

    private fun showLoading(state: Boolean) {
        binding.apply {
            if (state) {
                progressBar.visible()
                imgPoster.gone()
                cardMain.gone()
                cardMaps.gone()
            } else {
                progressBar.gone()
                imgPoster.visible()
                cardMain.visible()
                cardMaps.visible()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unloadKoinModules(profileHospitalModule)
        _binding = null
    }

    companion object {
        private const val ADDRESS_HOSPITAL = "address"
        private const val WEBSITE_URL = "website_url"
        private const val PHONE_HOSPITAL = "phone"
        private const val IMAGE_URL = "image_url"
    }
}