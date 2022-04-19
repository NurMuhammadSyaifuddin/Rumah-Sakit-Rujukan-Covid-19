package com.project.hospital_admin.ui.registration

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.rxbinding2.widget.RxTextView
import com.project.core.domain.model.Registration
import com.project.core.domain.model.User
import com.project.core.utils.*
import com.project.hospital_admin.R
import com.project.hospital_admin.databinding.ActivityCheckingRegistrationBinding
import com.project.hospital_admin.utils.showAlertDialogCheckingRegistration
import com.project.rumahsakitrujukancovid_19.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel

class CheckingRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckingRegistrationBinding

    private lateinit var loading: AlertDialog

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val currentUser by lazy { auth.currentUser }

    private val viewModel: CheckingRegistrationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckingRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init
        loading = showAlertLoading(this)

        onAction()
    }

    @SuppressLint("CheckResult")
    private fun onAction() {
        binding.apply {

            tvCharCount.text = getString(R.string.character_s_100, 0.toString())

            val noteStream = RxTextView.textChanges(edtNote)
                .skipInitialValue()
                .map { note ->
                    note.length > 50
                }

            noteStream.subscribe {
                showNoteExistAlert(it)
            }

            intent?.let {
                val registration = it.extras?.getParcelable<Registration>(EXTRA_DATA)

                btnReject.setOnClickListener {
                    hideSoftKeyboard(this@CheckingRegistrationActivity, binding.root)
                    showAlertDialogCheckingRegistration(
                        this@CheckingRegistrationActivity,
                        getString(R.string.are_you_sure_reject_this_registration)
                    ) {
                        loading.show()
                        processedRegistration(registration, this@CheckingRegistrationActivity.rejected())
                    }.show()
                }

                btnAccept.setOnClickListener {
                    hideSoftKeyboard(this@CheckingRegistrationActivity, binding.root)
                    showAlertDialogCheckingRegistration(
                        this@CheckingRegistrationActivity,
                        getString(R.string.will_you_acceot_this_registration)
                    ) {
                        loading.show()
                        processedRegistration(registration, this@CheckingRegistrationActivity.accepted())
                    }.show()
                }

                imgBack.setOnClickListener { finish() }

            }

        }
    }

    private fun processedRegistration(registration: Registration?, statusRegistration: String) {
        viewModel.collectionUser(db)
            .get()
            .addOnSuccessListener { query ->
                val user = query.documents.asSequence()
                    .map { document ->
                        document.toObject(User::class.java)
                    }
                    .filter { user ->
                        user?.id == currentUser?.uid.toString() && user.status == HOSPITAL_ADMIN
                    }
                    .take(1)
                    .toList()

                if (user.isNotEmpty()) {

                    viewModel.getCollectionRegistrationAdmin(db, user[0]?.email.toString())
                        .get()
                        .addOnSuccessListener { task ->
                            val registrations = task.documents.asSequence()
                                .map { snapshot ->
                                    snapshot.toObject(Registration::class.java)
                                }
                                .filter { value ->
                                    val dateArray =
                                        value?.registrationDate?.split(" ")?.toTypedArray()
                                    val date = dateArray?.get(0)
                                    registration?.referredTo == value?.referredTo && isCurrentTimeTheSame(
                                        date.toString()
                                    )
                                }
                                .sortedBy { data ->
                                    data?.registrationDate
                                }
                                .toList()

                            if (registrations.isNotEmpty()) {
                                val queue =
                                    if (statusRegistration == this.accepted()) {
                                        val index = registrations.indexOf(
                                            registration
                                        ) + 1
                                        if ( index == 0) 1 else index
                                    } else 0

                                val statusReferredTo =
                                    if (statusRegistration == this.accepted()) registration?.referredTo.toString() else getString(
                                        R.string.default_text
                                    )

                                updateRegistration(
                                    user,
                                    registration,
                                    statusRegistration,
                                    queue,
                                    statusReferredTo
                                )
                            }
                        }

                }
            }

    }

    private fun updateRegistration(
        user: List<User?>,
        registration: Registration?,
        statusRegistration: String,
        queue: Int,
        referredTo: String
    ) {

        val note = if (binding.edtNote.toString()
                .isNotBlank()
        ) binding.edtNote.text.toString() else getString(R.string.default_text)

        val acceptDate = if (statusRegistration == this.accepted()) getCurrentTime() else getString(R.string.default_text)

        viewModel.collectionRegistration(
            db,
            registration?.idUser.toString(),
            registration?.registrationNumber.toString()
        )
            .update(
                "note", note,
                "statusRegistration", statusRegistration,
                "acceptDate", acceptDate,
                "queue", queue,
                "referredTo", referredTo
            )

        viewModel.collectionRegistrationAdmin(
            db,
            user[0]?.email.toString(),
            registration?.registrationNumber.toString()
        )
            .update(
                "note", note,
                "statusRegistration", statusRegistration,
                "acceptDate", acceptDate,
                "queue", queue,
                "referredTo", referredTo
            )


        viewModel.getCollectionRegistrationAdmin(db, user[0]?.email.toString())
            .get()
            .addOnSuccessListener { query ->

                val data = query.documents.asSequence()
                    .map {
                        it.toObject(Registration::class.java)
                    }
                    .filter {
                        it?.registrationNumber == registration?.registrationNumber.toString()
                    }
                    .take(1)
                    .toList()

                if (data.isNotEmpty()) {
                    val intent = Intent(
                        this,
                        Class.forName("com.project.user.ui.registration.DetailRegistrationActivity")
                    ).also { intent ->
                        intent.putExtra(EXTRA_DATA_FOR_REGISTRATION, data[0])
                        finish()
                        loading.dismiss()
                    }

                    startActivity(intent)
                }

            }

        val titleStatusRegistration =
            if (statusRegistration == this.accepted()) getString(R.string.approved) else getString(R.string.rejected)

        showNotification(titleStatusRegistration, registration)

    }

    private fun showNotification(title: String, registration: Registration?) {
        val intentToDetailRegistration = Intent(
            this,
            Class.forName("com.project.user.ui.registration.DetailRegistrationActivity")
        ).also {
            it.putExtra(EXTRA_DATA_FOR_REGISTRATION, registration)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intentToDetailRegistration,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder =
            NotificationCompat.Builder(
                this,
                CHANNEL_ID
            )
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.status_registration, title))
                .setContentText(
                    getString(
                        R.string.status_registration_content,
                        registration?.name.toString(),
                        title,
                        registration?.registrationNumber.toString()
                    )
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            getString(
                                R.string.status_registration_content,
                                registration?.name.toString(),
                                title,
                                registration?.registrationNumber.toString()
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
    }

    private fun showNoteExistAlert(isNotValid: Boolean) {
        binding.apply {
            if (isNotValid) {
                textInputHospital.error = getString(R.string.note_max_100_char)
                btnAccept.disable()
                btnReject.disable()
            } else {
                textInputHospital.error = null
                btnAccept.enable()
                btnReject.enable()
            }

            lifecycleScope.launch(Dispatchers.Default) {
                val charNoteCount = edtNote.text.length.toString()

                withContext(Dispatchers.Main) {
                    tvCharCount.text = getString(R.string.character_s_100, charNoteCount)
                }
            }

        }
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
        private const val CHANNEL_ID = "channel_0101"
        private const val CHANNEL_NAME = "checking_registration"
        private const val NOTIFICATION_ID = 26
    }

}