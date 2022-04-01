package com.project.activity_user.notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.domain.model.Hospital
import com.project.core.domain.model.Registration
import com.project.core.utils.EXTRA_DATA_FOR_REGISTRATION
import com.project.core.utils.PATH_REGISTRATION
import com.project.history_user.R
import java.time.LocalDate
import java.util.*

class EventAfterOneDayRegistration : BroadcastReceiver() {

    private val db by lazy { FirebaseFirestore.getInstance() }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val hospital = intent.extras?.getParcelable<Hospital>(EXTRA_DATA_HOSPITAL)
        val registration = intent.extras?.getParcelable<Registration>(EXTRA_DATA_REGISTRATION)

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
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(
                context.getString(
                    R.string.otomatic_reject_registration,
                    hospital?.name
                )
            )
            .setContentText(context.getString(R.string.desc_otomatic_reject_registration))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(context.getString(R.string.desc_otomatic_reject_registration))
            )
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            builder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    @SuppressLint("InlinedApi")
    fun setUpAlarmAfterOneDayRegistration(context: Context, time: String, hospital: Hospital, registration: Registration){

        val date = java.sql.Date.valueOf(LocalDate.now().plusDays(1).toString()).toString()

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, EventAfterOneDayRegistration::class.java).also {
            it.putExtra(EXTRA_DATA_HOSPITAL, hospital)
            it.putExtra(EXTRA_DATA_REGISTRATION, registration)
        }

        val dateArray = date.split("-").toTypedArray()
        val timeArray = date.split(":").toTypedArray()

        val calendar = Calendar.getInstance().apply {
            set(Calendar.DATE, Integer.parseInt(dateArray[0]))
            set(Calendar.MONTH, Integer.parseInt(dateArray[1]) - 1)
            set(Calendar.YEAR, Integer.parseInt(dateArray[2]))
            set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
            set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
            set(Calendar.SECOND, Integer.parseInt(timeArray[2]))
        }

        val pendingIntent = PendingIntent.getBroadcast(context, ID_ONE_TIME, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    fun showAlarmNotification(context: Context, hospital: Hospital, notifId: Int){
        db.collection(PATH_REGISTRATION).document(hospital.name.toString())
            .get()
            .addOnCompleteListener { task ->
                val result = task.result?.toObject(Registration::class.java)
            }
    }

    companion object {
        private const val ID_ONE_TIME = 100
        const val EXTRA_DATA_HOSPITAL = "extra_data_hospital"
        const val EXTRA_DATA_REGISTRATION = "extra_data_registration"
        const val CHANNEL_ID = "channel_after_one_day_registration"
        const val CHANNEL_NAME = "channel_name_after_one_day_registration"
        const val NOTIFICATION_ID = 2

        private const val DATE_FORMAT = "dd-MM-yyyy"
        private const val TIME_FORMAT = "HH:mm:ss"

    }
}