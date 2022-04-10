package com.project.user.ui.detail

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.project.activity_user.notification.EventAfterOneDayRegistration
import com.project.core.domain.model.Hospital
import com.project.core.domain.model.User
import com.project.user.R
import com.project.user.databinding.ActivityDetailBinding
import com.project.user.di.homeModule
import com.project.core.domain.model.Registration
import com.project.core.utils.*
import com.project.core.utils.loadImage
import com.project.rumahsakitrujukancovid_19.utils.*
import com.project.user.ui.registration.DetailRegistrationActivity
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import timber.log.Timber
import java.lang.Exception

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val viewModel: DetailViewModel by viewModel()

    private lateinit var loading: AlertDialog

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { Firebase.firestore }
    private val user by lazy { FirebaseAuth.getInstance().currentUser }
    private val ref by lazy { FirebaseStorage.getInstance().reference }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadKoinModules(homeModule)

        // init
        loading = showAlertLoading(this)

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
                                    val result = query.result?.toObject(User::class.java)

                                    val id = getRandomIdString()
                                    val idUser = auth.currentUser?.uid.toString()
                                    val registrationNumber = getRandomIdNumber()
                                    val registrationDate = getCurrentTime()
                                    val name = result?.name.toString()
                                    val hospitalName = tvName.text.toString()
                                    val imageUrl = hospital.imageUrl.toString()

                                    viewModel.getCollectionRegistration(db, idUser)
                                        .get()
                                        .addOnSuccessListener { queryRegistration ->
                                            val data = queryRegistration.documents.asSequence()
                                                .map { value ->
                                                    value.toObject(Registration::class.java)
                                                }
                                                .filter { value ->
                                                    value?.idUser == idUser && value.hospitalName == hospitalName && value.statusRegistration == WAIT
                                                }
                                                .take(1)
                                                .toList()

                                            if (data.isNotEmpty()) {
                                                loading.dismiss()
                                                showAlertDialogAlreadyRegistered(
                                                    this@DetailActivity,
                                                    getString(
                                                        R.string.signed_up_today,
                                                        hospitalName
                                                    )
                                                ).show()
                                            } else {
                                                viewModel.storageReference(ref, idUser)
                                                    .downloadUrl
                                                    .addOnCompleteListener { task ->

                                                        val registrationForDetail = Registration(
                                                            id = id,
                                                            idUser = idUser,
                                                            registrationNumber = registrationNumber,
                                                            registrationDate = registrationDate,
                                                            name = name,
                                                            hospitalName = hospitalName,
                                                            imageUrl = imageUrl
                                                        )

                                                        if (task.isSuccessful) registrationForDetail.photoUrl =
                                                            task.result?.toString()
                                                        else registrationForDetail.photoUrl =
                                                            result?.photoUrl.toString()

                                                        sendRegistration(
                                                            id,
                                                            idUser,
                                                            registrationForDetail,
                                                            registrationNumber,
                                                            registrationDate,
                                                            name,
                                                            hospitalName,
                                                            imageUrl,
                                                            hospital
                                                        )
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
                }

            }
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendRegistration(
        id: String,
        idUser: String,
        registrationForDetail: Registration,
        registrationNumber: String,
        registrationDate: String,
        name: String,
        hospitalName: String,
        imageUrl: String,
        hospital: Hospital
    ) {
        val registration = hashMapOf(
            "id" to id,
            "idUser" to idUser,
            "photoUrl" to registrationForDetail.photoUrl.toString(),
            "registrationNumber" to registrationNumber,
            "registrationDate" to registrationDate,
            "name" to name,
            "hospitalName" to hospitalName,
            "imageUrl" to imageUrl,
            "acceptDate" to "",
            "statusRegistration" to WAIT,
            "note" to "",
            "queue" to 0,
            "typeActivities" to getString(R.string.registration),
            "isShowNotif" to false
        )

        viewModel.collectionRegistration(
            db,
            idUser,
            registrationNumber
        )
            .set(registration)

        viewModel.collectionRegistrationAdmin(
            db,
            hospital.emailAdmin.toString(),
            registrationNumber
        )
            .set(registration)

        val intent = Intent(
            this@DetailActivity,
            DetailRegistrationActivity::class.java
        ).also { intent ->
            intent.putExtra(
                EXTRA_DATA_FOR_REGISTRATION,
                registrationForDetail
            )
            loading.dismiss()
            finish()
        }

        startActivity(intent)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder =
            NotificationCompat.Builder(
                this@DetailActivity,
                CHANNEL_ID
            )
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.registration_wait_title))
                .setContentText(
                    getString(
                        R.string.register_wait,
                        hospitalName
                    )
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            getString(
                                R.string.register_wait,
                                hospitalName
                            )
                        )
                )
                .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            builder.setChannelId(CHANNEL_ID)

            notificationManager.createNotificationChannel(
                channel
            )
        }

        val notification = builder.build()

        notificationManager.notify(
            NOTIFICATION_ID,
            notification
        )

        val alarmReceiver = EventAfterOneDayRegistration()
        alarmReceiver.setUpAlarmAfterOneDayRegistration(
            this@DetailActivity,
            hospital,
            registrationForDetail
        )
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
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "registration_waiting"
        private const val NOTIFICATION_ID = 0
    }
}