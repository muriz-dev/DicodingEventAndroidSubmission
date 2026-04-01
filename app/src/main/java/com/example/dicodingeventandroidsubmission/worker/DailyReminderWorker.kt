package com.example.dicodingeventandroidsubmission.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dicodingeventandroidsubmission.R
import com.example.dicodingeventandroidsubmission.di.Injection
import com.example.dicodingeventandroidsubmission.ui.detail.DetailActivity

class DailyReminderWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_daily_reminder"
        const val CHANNEL_NAME = "dicoding event channel"
    }

    override suspend fun doWork(): Result {
        val repository = Injection.provideRepository(applicationContext)

        return try {
            val event = repository.getDailyReminderEvent()

            if (event != null) {
                showNotification(event.id.toString(), event.name, event.beginTime)
            }

            Result.success()
        } catch (_: Exception) {
            Result.failure()
        }
    }

    private fun showNotification(id: String, title: String, description: String?) {
        val detailIntent = Intent(applicationContext, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_EVENT_ID, id)
        }
        val pendingIntent = TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(detailIntent)
            getPendingIntent(
                0,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )
        }

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.round_notifications_24)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notification.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }
}