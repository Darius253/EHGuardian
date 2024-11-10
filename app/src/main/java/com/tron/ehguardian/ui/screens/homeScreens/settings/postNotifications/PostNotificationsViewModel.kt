package com.tron.ehguardian.ui.screens.homeScreens.settings.postNotifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tron.ehguardian.MainActivity
import com.tron.ehguardian.R

class Notifications(
    private val context: Context
) {

    companion object {
        const val NOTIFICATION_ID = 1 // Example notification ID
    }


    @SuppressLint("InlinedApi", "MissingPermission")
    fun dailyReminderNotification() {
        val name = "Daily Reminder"
        val descriptionText = "Daily Reminder to check blood pressure!"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel("daily_reminder", name, importance).apply {
            description = descriptionText
            enableLights(true)
            enableVibration(true)
            vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        }

        // Register the channel with the system
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val dailyRemainder = NotificationCompat.Builder(context, "daily_reminder")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("Daily Reminder")
            .setContentText("Maintaining healthy habits? Check your blood pressure.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)


        // Check for notification permission before showing the notification
            with(NotificationManagerCompat.from(context)) {

            // notificationId is a unique int for each notification that you must define.
            notificationManager.notify(NOTIFICATION_ID, dailyRemainder.build())
        }
    }
}
