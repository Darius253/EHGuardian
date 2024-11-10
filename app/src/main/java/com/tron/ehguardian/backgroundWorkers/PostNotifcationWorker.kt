package com.tron.ehguardian.backgroundWorkers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tron.ehguardian.ui.screens.homeScreens.settings.postNotifications.Notifications
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostNotificationsWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {


        return withContext(Dispatchers.IO) {
            val notifications = Notifications(applicationContext)

            return@withContext try {
                notifications.dailyReminderNotification()
                Result.success()
            } catch (e: Exception) {
                Result.failure()
            }

        }

    }


}
