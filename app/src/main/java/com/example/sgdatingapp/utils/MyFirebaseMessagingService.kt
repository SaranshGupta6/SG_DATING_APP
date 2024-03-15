package com.example.sgdatingapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.example.sgdatingapp.MainActivity
import com.example.sgdatingapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService(){
    private val channelId = "saranshcoders"

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val inte = Intent(this,MainActivity::class.java)
        inte.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE)
        createNotificationChannel(manager as NotificationManager)

        val intel = PendingIntent.getActivities(this,0, arrayOf(inte),PendingIntent.FLAG_IMMUTABLE)

        val notification= NotificationCompat.Builder(this,channelId)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setAutoCancel(true)
            .setContentIntent(intel)
            .build()

       manager.notify(Random.nextInt(),notification)
    }

    private fun createNotificationChannel(manager: NotificationManager)
    {
        val channel=NotificationChannel(channelId,"saranshcoderschat",NotificationManager.IMPORTANCE_HIGH)

        channel.description="New Chat"
        channel.enableLights(true)

        manager.createNotificationChannel(channel)

    }
}