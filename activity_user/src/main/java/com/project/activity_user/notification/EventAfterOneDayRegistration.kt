package com.project.activity_user.notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.domain.model.Hospital
import com.project.core.domain.model.Registration
import com.project.core.utils.*
import com.project.history_user.R
import com.project.rumahsakitrujukancovid_19.utils.PATH_USER
import java.time.LocalDate
import java.util.*

class EventAfterOneDayRegistration : BroadcastReceiver() {

    private val db by lazy { FirebaseFirestore.getInstance() }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val hospital = intent.extras?.getParcelable<Hospital>(EXTRA_DATA_HOSPITAL)
        val registration = intent.extras?.getParcelable<Registration>(EXTRA_DATA_REGISTRATION)

        db.collection(PATH_REGISTRATION).document(PATH_USER).collection(registration?.idUser.toString())
            .get()
            .addOnSuccessListener { query ->
                val data = query.documents.asSequence()
                    .map {
                        it.toObject(Registration::class.java)
                    }
                    .filter {
                        it?.hospitalName == hospital?.name.toString() && it.statusRegistration == WAIT && it.registrationNumber == registration?.registrationNumber.toString()
                    }
                    .take(1)
                    .toList()

                if (data.isNotEmpty()){
                    val registrationMap = hashMapOf(
                        "id" to registration?.id,
                        "idUser" to registration?.idUser,
                        "photoUrl" to registration?.photUrl,
                        "registrationNumber" to registration?.registrationNumber,
                        "registrationDate" to registration?.registrationDate,
                        "name" to registration?.name,
                        "hospitalName" to registration?.hospitalName,
                        "imageUrl" to registration?.imageUrl,
                        "acceptDate" to "",
                        "statusRegistration" to REJECT,
                        "note" to context.getString(R.string.reject_by_system),
                        "queue" to 0,
                        "typeActivities" to context.getString(R.string.registration)
                    )

                    db.collection(PATH_REGISTRATION).document(PATH_USER).collection(registration?.idUser.toString()).document(registration?.registrationNumber.toString())
                        .set(registrationMap)

                    db.collection(PATH_REGISTRATION).document(PATH_ADMIN).collection(hospital?.name.toString()).document(registration?.registrationNumber.toString())
                        .set(registrationMap)

                    val intentToDetailRegistration = Intent(
                        context,
                        Class.forName("com.project.user.ui.registration.DetailRegistrationActivity")
                    ).also {
                        it.putExtra(EXTRA_DATA_FOR_REGISTRATION, registration)
                    }

                    val pendingIntent = PendingIntent.getActivity(
                        context,
                        0,
                        intentToDetailRegistration,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                        .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
                        .setSound(alarmSound)
                        .setContentTitle(
                            context.getString(
                                R.string.otomatic_reject_registration,
                                hospital?.name.toString()
                            )
                        )
                        .setContentText(context.getString(R.string.desc_otomatic_reject_registration, hospital?.name.toString()))
                        .setStyle(
                            NotificationCompat.BigTextStyle()
                                .bigText(context.getString(R.string.desc_otomatic_reject_registration, hospital?.name.toString()))
                        )
                        .setAutoCancel(true)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel = NotificationChannel(
                            CHANNEL_ID,
                            CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_DEFAULT
                        ).apply {
                            enableVibration(true)
                            vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
                        }

                        builder.setChannelId(CHANNEL_ID)
                        notificationManager.createNotificationChannel(channel)
                    }

                    val notification = builder.build()
                    notificationManager.notify(NOTIFICATION_ID, notification)
                }
            }

    }

    @SuppressLint("InlinedApi")
    fun setUpAlarmAfterOneDayRegistration(context: Context, hospital: Hospital, registration: Registration){

        val date = java.sql.Date.valueOf(LocalDate.now().plusDays(1).toString()).toString().split("-").toTypedArray()

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, EventAfterOneDayRegistration::class.java).also {
            it.putExtra(EXTRA_DATA_HOSPITAL, hospital)
            it.putExtra(EXTRA_DATA_REGISTRATION, registration)
        }


        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, Integer.parseInt(date[0]))
            set(Calendar.MONTH, Integer.parseInt(date[1]) - 1)
            set(Calendar.DATE, Integer.parseInt(date[2]))
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val pendingIntent = PendingIntent.getBroadcast(context, ID_ONE_TIME++, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    companion object {
        private var ID_ONE_TIME = 1
        const val EXTRA_DATA_HOSPITAL = "extra_data_hospital"
        const val EXTRA_DATA_REGISTRATION = "extra_data_registration"
        const val CHANNEL_ID = "channel_after_one_day_registration"
        const val CHANNEL_NAME = "channel_name_after_one_day_registration"
        const val NOTIFICATION_ID = 2
    }
}