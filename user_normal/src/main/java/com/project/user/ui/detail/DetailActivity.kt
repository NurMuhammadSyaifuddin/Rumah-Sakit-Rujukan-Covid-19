package com.project.user.ui.detail

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.core.domain.model.Hospital
import com.project.core.domain.model.User
import com.project.user.R
import com.project.user.databinding.ActivityDetailBinding
import com.project.user.di.homeModule
import com.project.core.domain.model.Registration
import com.project.core.utils.*
import com.project.core.utils.loadImage
import com.project.rumahsakitrujukancovid_19.utils.*
import com.project.user.ui.registration.ChooseDateForRegistrationActivity
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import timber.log.Timber
import java.lang.Exception

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val viewModel: DetailViewModel by viewModel()

    private lateinit var registrationDateFormatter: RegistrationDateFormatter

    private lateinit var loading: AlertDialog

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { Firebase.firestore }
    private val user by lazy { FirebaseAuth.getInstance().currentUser }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadKoinModules(homeModule)

        // init
        loading = showAlertLoading(this)
        registrationDateFormatter = RegistrationDateFormatter()

        getDataIntentForDetail()

    }

    @SuppressLint("ObsoleteSdkInt")
    private fun getDataIntentForDetail() {
        binding.apply {
            intent?.let {
                val hospital = it.extras?.getParcelable<Hospital>(EXTRA_DATA_FOR_DETAIL)

                imgPoster.loadImage(hospital?.imageUrl.toString())
                tvName.text = hospital?.name
                tvAddress.text = hospital?.address
                tvWebsiteUrl.text = hospital?.websiteUrl
                tvTelephone.text = hospital?.phone

                icCopy.setOnClickListener {
                    copyToClipBoard(hospital?.phone.toString())
                    this@DetailActivity.showToast(
                        resources.getString(
                            R.string.phone_is_copied,
                            hospital?.phone.toString()
                        )
                    )
                }

                imgBack.setOnClickListener { finish() }

                getStatusBookmark(hospital as Hospital)

                btnStartNavigation.setOnClickListener {
                    val hospitalNameFormat = hospital.name?.replace("\\s".toRegex(), "+")
                    val destination = Uri.parse("google.navigation:q=$hospitalNameFormat")
                    Intent(Intent.ACTION_VIEW, destination).also { intent ->
                        intent.setPackage("com.google.android.apps.maps")
                        startActivity(intent)
                    }
                }

                btnRegister.setOnClickListener {

                    if (registrationDateFormatter.isWeekdays(this@DetailActivity)) {

                        if (hospital.emailAdmin == "-") {
                            showAlertDialogAlreadyRegistered(
                                this@DetailActivity,
                                getString(R.string.not_yet_admin, hospital.name)
                            ).show()
                        } else {
                            loading.show()
                            try {
                                viewModel.collectionUser(db, auth.currentUser?.uid.toString())
                                    .get()
                                    .addOnCompleteListener { query ->
                                        val user = query.result?.toObject(User::class.java)

                                        val idUser = auth.currentUser?.uid.toString()
                                        val hospitalName = tvName.text.toString()

                                        viewModel.getCollectionRegistration(db, idUser)
                                            .get()
                                            .addOnSuccessListener { queryRegistration ->
                                                val data = queryRegistration.documents.asSequence()
                                                    .map { value ->
                                                        value.toObject(Registration::class.java)
                                                    }
                                                    .filter { value ->
                                                        val date = value?.registrationDate?.split(" ")?.toTypedArray()
                                                        val dateArray = date?.get(0).toString()
                                                        value?.idUser == idUser && value.hospitalName == hospitalName && value.statusRegistration != this@DetailActivity.rejected() && isCurrentTimeTheSame(dateArray)
                                                    }
                                                    .take(1)
                                                    .toList()

                                                if (data.isNotEmpty()) {
                                                    loading.dismiss()

                                                    val message =
                                                        if (data[0]?.statusRegistration == this@DetailActivity.wait()) getString(
                                                            R.string.signed_up_today,
                                                            hospitalName
                                                        )
                                                        else getString(R.string.signed_up_today_accept)

                                                    showAlertDialogAlreadyRegistered(
                                                        this@DetailActivity,
                                                        message
                                                    ).show()
                                                } else {
                                                    Intent(
                                                        this@DetailActivity,
                                                        ChooseDateForRegistrationActivity::class.java
                                                    ).also { intent ->
                                                        intent.putExtra(
                                                            ChooseDateForRegistrationActivity.EXTRA_DATA_HOSPITAL,
                                                            hospital
                                                        )
                                                        intent.putExtra(
                                                            ChooseDateForRegistrationActivity.EXTRA_DATA_USER,
                                                            user
                                                        )
                                                        startActivity(intent)
                                                        loading.dismiss()
                                                    }
                                                }
                                            }
                                    }
                                    .addOnFailureListener { error ->
                                        showToast(error.message.toString())
                                    }

                            } catch (e: Exception) {
                                Timber.e(e.message.toString())
                            }
                        }
                    } else {
                        showAlertDialogAlreadyRegistered(
                            this@DetailActivity,
                            getString(R.string.only_weekdays_to_registration)
                        ).show()
                    }

                }
            }
        }
    }


    private fun getStatusBookmark(hospital: Hospital) {
        viewModel.setCollectionHospitalFavorite(db, user?.uid.toString(), hospital.id.toString())
            .get()
            .addOnSuccessListener {
                var statusBookmark = it.getBoolean(IS_FAVORITE) ?: false
                setStatusBookmark(statusBookmark)

                binding.btnBookmark.setOnClickListener {
                    statusBookmark = !statusBookmark

                    if (!statusBookmark) binding.root.showSnackBar(
                        resources.getString(
                            R.string.remove_bookmark,
                            hospital.name
                        )
                    )
                    else binding.root.showSnackBar(
                        resources.getString(
                            R.string.add_bookmark,
                            hospital.name
                        )
                    )

                    setStatusBookmark(statusBookmark)

                    if (statusBookmark)
                        addDataToFirestore(statusBookmark, hospital)
                    else
                        deleteBookmark(hospital)
                }
            }

    }

    private fun deleteBookmark(data: Hospital) {
        viewModel.setCollectionHospitalFavorite(db, user?.uid.toString(), data.id.toString())
            .delete()
    }

    private fun addDataToFirestore(status: Boolean, data: Hospital) {
        val hospital = hashMapOf(
            "id" to data.id,
            "name" to data.name,
            "address" to data.address,
            "province" to data.province,
            "region" to data.region,
            "phone" to data.phone,
            "imageUrl" to data.imageUrl,
            "websiteUrl" to data.websiteUrl,
            "latitude" to data.latitude,
            "longitude" to data.longitude,
            "isFavorite" to status
        )

        viewModel.setCollectionHospitalFavorite(db, user?.uid.toString(), data.id.toString())
            .set(hospital)
    }

    private fun setStatusBookmark(state: Boolean) {
        binding.apply {
            if (state) {
                btnBookmark.setImageResource(R.drawable.ic_bookmark)
                btnBookmark.imageTintList = ColorStateList.valueOf(Color.RED)
            } else {
                btnBookmark.setImageResource(R.drawable.ic_out_bookmark)
                btnBookmark.imageTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.textColorPrimary, null))
            }
        }

    }

    private fun copyToClipBoard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(getString(R.string.label), text)
        clipboard.setPrimaryClip(clip)
    }

    override fun onDestroy() {
        super.onDestroy()
        unloadKoinModules(homeModule)
    }

    companion object {
        private const val IS_FAVORITE = "isFavorite"
    }
}