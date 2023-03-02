package com.lzpavel.chargingcontroller


import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


object ChargingNotification {

    val CHANNEL_ID = "ChargingNotification"

    fun createChannel(notificationManager: NotificationManager) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        val name = "ChargingNotification" //channel_name
        val descriptionText = "Charging notification for service" //channel_description
        //val importance = NotificationManager.IMPORTANCE_DEFAULT
        val importance = NotificationManager.IMPORTANCE_MIN
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }


        // Register the channel with the system
        //val notificationManager: NotificationManager =
        //getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //getSystemService(NotificationManager::class.java) as NotificationManager
        notificationManager.createNotificationChannel(channel)



    }

    fun build(context: Context, text: String = ""): Notification {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)


        var builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.lightning)
            .setContentTitle("Charging")
            .setContentText(text)
            .setTicker("Charging")
            //.setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .setContentIntent(pendingIntent)

        return builder.build()

    }

    fun show(context: Context, text: String = "") {
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            val notificationId = 1
            //notify(notificationId, builder.build())
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(notificationId, build(context, text))
        }
    }



}