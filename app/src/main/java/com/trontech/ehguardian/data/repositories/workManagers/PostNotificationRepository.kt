package com.trontech.ehguardian.data.repositories.workManagers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.trontech.ehguardian.backgroundWorkers.PostNotificationsWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class PostNotificationRepository(context: Context){
    private val workManager = WorkManager.getInstance(context)

    fun scheduleDailyNotification() {
        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            if (currentTime.get(Calendar.HOUR_OF_DAY) >= 8) {
                // Set to 9 PM if after 9 AM, or move to next day if it's past 9 PM
                set(Calendar.HOUR_OF_DAY, 20)
            } else {
                // Set to the next 9 AM
                set(Calendar.HOUR_OF_DAY, 8)
            }
            set(Calendar.MINUTE, t0)
            set(Calendar.SECOND, 0)
        }

        // Calculate the initial delay
        val initialDelay = targetTime.timeInMillis - currentTime.timeInMillis



        val notificationWorkRequest = PeriodicWorkRequestBuilder<PostNotificationsWorker>(
            12, TimeUnit.HOURS,  // Repeat every 12 hours
            1, TimeUnit.HOURS     // Flex interval of 1 hour
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS) // Start at target time
            .build()

        workManager.enqueueUniquePeriodicWork(
            "TwiceDailyNotificationWork",
            ExistingPeriodicWorkPolicy.KEEP,
            notificationWorkRequest
        )
    }




}