package com.project.hospital_admin.norification

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.domain.model.Registration
import com.project.core.domain.model.User
import com.project.core.utils.*
import com.project.hospital_admin.R
import com.project.rumahsakitrujukancovid_19.utils.HOSPITAL_ADMIN
import com.project.rumahsakitrujukancovid_19.utils.PATH_USER
import java.util.*

class ReceiveRegistrationService: BroadcastReceiver() {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onReceive(context: Context?, intent: Intent?) {

        db.collection(PATH_USER)
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

                if (user.isNotEmpty()){
                    db.collection(PATH_REGISTRATION).document(PATH_ADMIN).collection(user[0]?.email.toString())
                        .get()
                        .addOnSuccessListener { queryAdmin ->
                            val registrations = queryAdmin.documents
                                .map {
                                    it.toObject(Registration::class.java)
                                }
                                .filter {
                                    it?.statusRegistration == WAIT && !it.isShowNotif!!
                                }

                            for (index in registrations.indices){
                                showNotification(context as Context, index, registrations)
                            }

                        }
                }
            }
    }

    private fun showNotification(context: Context, index: Int, registrations: List<Registration?>) {

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setSound(alarmSound)
            .setContentTitle(
                context.getString(
                    R.string.new_registration
                )
            )
            .setContentText(context.getString(R.string.new_registration_from_user, registrations[index]?.name.toString()))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(context.getString(R.string.new_registration_from_user, registrations[index]?.name.toString()))
            )
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
        db.collection(PATH_USER)
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

                if (user.isNotEmpty()){
                    val registration = registrations[index]
                    db.collection(PATH_REGISTRATION).document(PATH_USER)
                        .collection(registration?.idUser.toString())
                        .document(registration?.registrationNumber.toString())
                        .update(
                            "isShowNotif", true
                        )

                    db.collection(PATH_REGISTRATION).document(PATH_ADMIN)
                        .collection(user[0]?.email.toString())
                        .document(registration?.registrationNumber.toString())
                        .update(
                            "isShowNotif", true
                        )
                }
            }
    }

    @SuppressLint("InlinedApi")
    fun setUpRepeatingAlarm(context: Context){

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReceiveRegistrationService::class.java)

        val date = getCurrentTime()

        val dateTime = date.split(" ").toTypedArray()
        val timeArray = dateTime[1].split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
            set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
            set(Calendar.SECOND, Integer.parseInt(timeArray[2]))
        }

        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, 1000, pendingIntent)
    }

    fun cancelRepeatingAlarm(context: Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReceiveRegistrationService::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, intent, PendingIntent.FLAG_IMMUTABLE)
        pendingIntent.cancel()

        alarmManager.cancel(pendingIntent)
    }

    companion object {
        private var ID_REPEATING = 100
        private const val CHANNEL_ID = "channel_after_one_day_registration"
        private const val CHANNEL_NAME = "channel_name_after_one_day_registration"
        private const val NOTIFICATION_ID = 2
    }
}