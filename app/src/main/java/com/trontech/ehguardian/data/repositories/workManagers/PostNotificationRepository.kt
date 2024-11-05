package com.trontech.ehguardian.data.repositories.workManagers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.trontech.ehguardian.backgroundWorkers.PostNotificationsWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class PostNotificationRepository(context: Context) {
    private val workManager = WorkManager.getInstance(context)

    fun scheduleDailyNotification() {
        try {
            val currentTime = Calendar.getInstance()
            val targetTime = Calendar.getInstance().apply {
                when {
                    // If between midnight and 8 AM, set for 8 AM today
                    currentTime.get(Calendar.HOUR_OF_DAY) < 8 -> {
                        set(Calendar.HOUR_OF_DAY, 8)
                    }
                    // If between 8 AM and 8 PM, set for 8 PM today
                    currentTime.get(Calendar.HOUR_OF_DAY) < 20 -> {
                        set(Calendar.HOUR_OF_DAY, 20)
                    }
                    // If after 8 PM, set for 8 AM tomorrow
                    else -> {
                        add(Calendar.DAY_OF_YEAR, 1)
                        set(Calendar.HOUR_OF_DAY, 8)
                    }
                }
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // Calculate initial delay in milliseconds
            val initialDelay = (targetTime.timeInMillis - currentTime.timeInMillis)
                .coerceAtLeast(0L) // Ensure non-negative delay

            val notificationWorkRequest = PeriodicWorkRequestBuilder<PostNotificationsWorker>(
                12, TimeUnit.HOURS
            ).apply {
                setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            }.build()

            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE, // UPDATE instead of KEEP to ensure latest constraints
                notificationWorkRequest
            )
        } catch (e: Exception) {
            // Log error or handle appropriately
            e.printStackTrace()
        }
    }

    companion object {
        private const val WORK_NAME = "TwiceDailyNotificationWork"
    }
}