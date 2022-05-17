package com.project.user.ui.registration

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.activity_user.notification.EventAfterOneDayRegistration
import com.project.core.domain.model.Hospital
import com.project.core.domain.model.Registration
import com.project.core.domain.model.User
import com.project.core.utils.*
import com.project.rumahsakitrujukancovid_19.utils.*
import com.project.user.R
import com.project.user.databinding.ActivityChooseDateForRegistrationBinding
import org.koin.android.viewmodel.ext.android.viewModel

class ChooseDateForRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseDateForRegistrationBinding

    private lateinit var registrationDateFormatter: RegistrationDateFormatter

    private val viewModel: RegistrationViewModel by viewModel()

    private lateinit var loading: androidx.appcompat.app.AlertDialog

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val ref by lazy { FirebaseStorage.getInstance().reference }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val currentUser by lazy { auth.currentUser }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseDateForRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init
        registrationDateFormatter = RegistrationDateFormatter()
        loading = showAlertLoading(this)

        onAction()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onAction() {
        binding.apply {

            val data =
                registrationDateFormatter.getRangeDayOfRegistrationDate(this@ChooseDateForRegistrationActivity)

            edtRegistrationChoose.setOnTouchListener { _, _ ->
                val adapter = ArrayAdapter(
                    this@ChooseDateForRegistrationActivity,
                    android.R.layout.select_dialog_item,
                    data
                )
                adapter.notifyDataSetChanged()
                edtRegistrationChoose.setAdapter(adapter)

                edtRegistrationChoose.showDropDown()
                edtRegistrationChoose.requestFocus()

                false
            }


            edtRegistrationChoose.setOnClickListener {
                btnOk.enable()
            }

            imgBack.setOnClickListener { finish() }

            btnOk.setOnClickListener {
                loading.show()

                intent?.let {
                    val hospital = it.extras?.getParcelable<Hospital>(EXTRA_DATA_HOSPITAL)
                    val user = it.extras?.getParcelable<User>(EXTRA_DATA_USER)

                    val id = getRandomIdString()
                    val idUser = currentUser?.uid.toString()
                    val registrationNumber = getRandomIdNumber() + user?.id?.takeLast(4)
                    val registrationDate = getCurrentTime()
                    val name = user?.name.toString()
                    val imageUrl = hospital?.imageUrl.toString()
                    val hospitalName = hospital?.name.toString()
                    val referredTo = edtRegistrationChoose.text.toString()

                    viewModel.storageReference(ref, idUser)
                        .downloadUrl
                        .addOnCompleteListener { task ->

                            val registrationForDetail =
                                Registration(
                                    id = id,
                                    idUser = idUser,
                                    registrationNumber = registrationNumber,
                                    registrationDate = registrationDate,
                                    name = name,
                                    hospitalName = hospitalName,
                                    imageUrl = imageUrl,
                                    referredTo = referredTo,
                                    statusRegistration = this@ChooseDateForRegistrationActivity.wait(),
                                    emailAdmin = hospital?.emailAdmin.toString()
                                )

                            if (task.isSuccessful) registrationForDetail.photoUrl =
                                task.result?.toString()
                            else registrationForDetail.photoUrl =
                                user?.photoUrl.toString()

                            sendRegistration(
                                registrationForDetail,
                                hospital as Hospital
                            )
                        }

                }

            }

        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendRegistration(
        registrationForDetail: Registration,
        hospital: Hospital
    ) {
        val registration = hashMapOf(
            "id" to registrationForDetail.id.toString(),
            "idUser" to registrationForDetail.idUser.toString(),
            "photoUrl" to registrationForDetail.photoUrl.toString(),
            "registrationNumber" to registrationForDetail.registrationNumber.toString(),
            "registrationDate" to registrationForDetail.registrationDate.toString(),
            "name" to registrationForDetail.name.toString(),
            "hospitalName" to registrationForDetail.hospitalName.toString(),
            "emailAdmin" to registrationForDetail.emailAdmin.toString(),
            "imageUrl" to registrationForDetail.imageUrl.toString(),
            "acceptDate" to "",
            "statusRegistration" to this.wait(),
            "note" to "",
            "queue" to 0,
            "typeActivities" to getString(R.string.registration),
            "isShowNotif" to false,
            "referredTo" to registrationForDetail.referredTo.toString()
        )

        viewModel.collectionRegistration(
            db,
            registrationForDetail.idUser.toString(),
            registrationForDetail.registrationNumber.toString()
        )
            .set(registration)

        viewModel.collectionRegistrationAdmin(
            db,
            hospital.emailAdmin.toString(),
            registrationForDetail.registrationNumber.toString()
        )
            .set(registration)

        val intent = Intent(
            this,
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
                this,
                CHANNEL_ID
            )
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle(getString(R.string.registration_wait_title))
                .setContentText(
                    getString(
                        R.string.register_wait,
                        registrationForDetail.hospitalName
                    )
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            getString(
                                R.string.register_wait,
                                registrationForDetail.hospitalName
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
            this,
            hospital,
            registrationForDetail
        )
    }

    companion object{
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "registration_waiting"
        private const val NOTIFICATION_ID = 0
        const val EXTRA_DATA_HOSPITAL = "extra_data_hospital"
        const val EXTRA_DATA_USER = "extra_data_user"
    }
}