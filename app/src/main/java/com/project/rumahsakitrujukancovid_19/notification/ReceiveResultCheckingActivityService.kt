package com.project.rumahsakitrujukancovid_19.notification

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.domain.model.Registration
import com.project.core.utils.*
import com.project.rumahsakitrujukancovid_19.R
import com.project.rumahsakitrujukancovid_19.utils.PATH_USER
import java.util.*

class ReceiveResultCheckingActivityService : BroadcastReceiver() {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onReceive(context: Context?, intent: Intent?) {
        db.collection(PATH_REGISTRATION).document(PATH_USER)
            .collection(auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { queryUser ->
                val registrations = queryUser.documents
                    .map {
                        it.toObject(Registration::class.java)
                    }
                    .filter {
                        it?.statusRegistration != context?.wait() && !it?.isShowNotif!!
                    }

                if (registrations.isNotEmpty()) {
                    for (index in registrations.indices) {
                        showNotification(context as Context, index, registrations)
                    }
                }

            }
    }

    private fun showNotification(context: Context, index: Int, registrations: List<Registration?>) {

        val registration = registrations[index]

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

        val title =
            if (registration?.statusRegistration == context.accepted()) context.getString(R.string.registration_successful)
            else context.getString(R.string.registration_rejected)

        val desc = if (registration?.statusRegistration == context.accepted()) context.getString(
            R.string.registration_accept_by_admin,
            registration.hospitalName.toString()
        )
        else context.getString(
            R.string.registration_rejected_by_admin,
            registration?.hospitalName.toString()
        )

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher)
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setSound(alarmSound)
            .setContentTitle(
                title
            )
            .setContentText(
                desc
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        desc
                    )
            )
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = CHANNEL_ID + getRandomStringSingle()
            val channelName = CHANNEL_NAME + getRandomStringSingle()

            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                enableVibration(true)
                vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            }

            builder.setChannelId(channelId)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManager.notify(NOTIFICATION_ID + getRandomIntSingle(), notification)

        setNotificationIsShow(index, registrations)
    }

    private fun setNotificationIsShow(index: Int, registrations: List<Registration?>) {

        val registration = registrations[index]

        db.collection(PATH_REGISTRATION).document(PATH_USER)
            .collection(auth.currentUser?.uid.toString())
            .document(registration?.registrationNumber.toString())
            .update(
                "isShowNotif", true
            )

    }


    @SuppressLint("InlinedApi")
    fun setUpRepeatingAlarm(context: Context) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReceiveResultCheckingActivityService::class.java)

        val date = getCurrentTime()

        val dateTime = date.split(" ").toTypedArray()
        val timeArray =
            dateTime[1].split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
            set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
            set(Calendar.SECOND, Integer.parseInt(timeArray[2]))
        }

        val pendingIntent =
            PendingIntent.getBroadcast(context, ID_REPEATING, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000,
            pendingIntent
        )
    }

    fun cancelRepeatingAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReceiveResultCheckingActivityService::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, ID_REPEATING, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        pendingIntent.cancel()

        alarmManager.cancel(pendingIntent)
    }

    companion object {
        private var ID_REPEATING = 3
        private const val CHANNEL_ID = "channel_repeating_user"
        private const val CHANNEL_NAME = "channel_name_repeating_user"
        private const val NOTIFICATION_ID = 11
    }

}