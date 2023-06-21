package com.example.fooddeliveryapp.Notif

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.Repository.UsersRespository
import com.example.fooddeliveryapp.viewmodel.UsersViewModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.threads.PoolableExecutors.factory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MyFirebaseMessagingService : FirebaseMessagingService() {

    var userRepo= UsersRespository()
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle incoming FCM messages here

        // Example: Display a notification
        showNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
    }

    private fun showNotification(title: String?, message: String?) {
        val channelId = "default_channel_id"
        val channelName = "Default Channel"
        val notificationId = 1

        val largeIconBitmap = BitmapFactory.decodeResource(resources, R.drawable.top_back)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.top_back) // Set a valid small icon
            .setContentTitle(title)
            .setContentText(message)
            .setLargeIcon(largeIconBitmap)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the notification channel (required for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableLights(true)
            channel.lightColor = Color.BLUE
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }


override fun onCreate() {
        super.onCreate()

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)


        if(checkUserAuth()) {
            CoroutineScope(Dispatchers.IO).launch{
                val resutl = userRepo.addToken(getUserId(),token)
                delay(500)
            }
        }
    }


    fun getUserId():Int {//recuperer le user_id sauvegardé
        val sharedPrf = applicationContext.getSharedPreferences("Auth", Context.MODE_PRIVATE)
        val uid  = sharedPrf.getInt("userId",-1)
        return uid
    }

    fun checkUserAuth():Boolean {//verifier si le user est authentifié
        val sharedPrf = applicationContext.getSharedPreferences("Auth", Context.MODE_PRIVATE)
        val editor = sharedPrf.edit()
        val auth = sharedPrf.getBoolean("auth",false)
        return auth
    }
}