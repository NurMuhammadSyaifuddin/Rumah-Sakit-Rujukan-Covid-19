package com.project.profile_user

import android.app.Activity
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.core.domain.model.User
import com.project.core.utils.loadImage
import com.project.profile_user.databinding.FragmentProfileUserBinding
import com.project.profile_user.di.profileUserModule
import com.project.profile_user.utils.showAlertDialogEditName
import com.project.profile_user.utils.showBottomSheetDialogEditImage
import com.project.rumahsakitrujukancovid_19.ui.login.LoginActivity
import com.project.rumahsakitrujukancovid_19.utils.showToast
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import timber.log.Timber
import java.io.ByteArrayOutputStream

class ProfileUserFragment : Fragment() {

    private var _binding: FragmentProfileUserBinding? = null
    private lateinit var binding: FragmentProfileUserBinding

    private val viewModel: ProfileUserViewModel by viewModel()

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val user by lazy { auth.currentUser }
    private val ref by lazy { FirebaseStorage.getInstance().reference }

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

        viewModel.storageReference(ref, user?.uid.toString())
            .putFile(uri)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    getUserProfile()
                    (activity as FragmentActivity).showToast(getString(R.string.image_profile_updated))
                }
            }.addOnFailureListener {
                (activity as FragmentActivity).showToast(it.message.toString())
            }
    }

    private fun handleImageResult(task: Intent?) {
        val bitmap = task?.extras?.get("data") as Bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        viewModel.storageReference(ref, user?.uid.toString())
            .putBytes(data)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    getUserProfile()
                    (activity as FragmentActivity).showToast(getString(R.string.image_profile_updated))
                }
            }
            .addOnFailureListener {
                (activity as FragmentActivity).showToast(it.message.toString())
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileUserBinding.inflate(inflater, container, false)
        if (_binding != null) {
            binding = _binding as FragmentProfileUserBinding
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadKoinModules(profileUserModule)

        onAction()
    }

    private fun onAction() {
        binding.apply {

            getUserProfile()

            btnLogoutUser.setOnClickListener {
                auth.signOut()
                Intent(activity, LoginActivity::class.java).also { intent ->
                    startActivity(intent)
                    activity?.finishAffinity()
                }
            }

            btnEditName.setOnClickListener {
                showAlertDialogEditName(
                    activity as FragmentActivity,
                    tvNameUser.text.toString()
                ) { name ->
                    viewModel.collectionUser(db, user?.uid.toString())
                        .update(
                            "name", name
                        )
                        .addOnSuccessListener {
                            getUserProfile()
                            (activity as FragmentActivity).showToast(getString(R.string.name_change_success))
                        }
                        .addOnFailureListener {
                            (activity as FragmentActivity).showToast(it.message.toString())
                        }
                }.show()
            }

            btnEditImage.setOnClickListener {
                getPickerEditImage()
            }
        }
    }

    private fun getPickerEditImage() {
        showBottomSheetDialogEditImage(activity as FragmentActivity,
            {
                removeProfile()
            },
            {
                openCamera()
            },
            {
                openGallery()
            }
        ).show()
    }

    private fun removeProfile() {
        try {
            viewModel.storageReference(ref, user?.uid.toString())
                .delete()
                .addOnSuccessListener {
                    getUserProfile()
                    (activity as FragmentActivity).showToast(getString(R.string.delete_image_success))
                }
                .addOnFailureListener {
                    (activity as FragmentActivity).showToast(it.message.toString())
                }
        } catch (e: Exception) {
            Timber.d(e.message.toString())
        }
    }

    private fun openGallery() {
        Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).also { takePictureIntent ->
            takePictureIntent.resolveActivity((activity as FragmentActivity).packageManager).also {
                launchGallery.launch(takePictureIntent)
            }
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            launchCamera.launch(takePictureIntent)
        }
    }

    private fun getUserProfile() {
        binding.apply {
            try {
                viewModel.collectionUser(db, user?.uid.toString())
                    .get()
                    .addOnCompleteListener { query ->
                        val result = query.result?.toObject(User::class.java)

                        viewModel.storageReference(ref, user?.uid.toString())
                            .downloadUrl
                            .addOnCompleteListener {
                                if (it.isSuccessful) imgAvatarProfile.loadImage(it.result.toString())
                                else imgAvatarProfile.loadImage(result?.photoUrl.toString())

                            }
                            .addOnFailureListener {
                                imgAvatarProfile.loadImage(result?.photoUrl.toString())
                            }

                        tvNameUser.text = result?.name.toString()
                        tvEmailUser.text = result?.email.toString()
                    }
            } catch (e: Exception) {
                Timber.d(e.message.toString())
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unloadKoinModules(profileUserModule)
        _binding = null
    }

}