package com.example.customtimer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters


@Suppress("DEPRECATION")
class NotificationWorker(appContext: Context, workerParams: WorkerParameters) : Worker(
    appContext,
    workerParams
) {
    companion object {
        const val DEFAULT_CHANNEL_ID = "0"
        const val REQUEST_CODE = 111
    }

    private val context = applicationContext
    private val timerValue = inputData.getInt("timer", 0)
    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun doWork(): Result {
        Log.d("NotificationWorker", "Preparing to show notification")
        createNotificationChannel()
        val notification = buildNotification()
        notificationManager.notify(REQUEST_CODE, notification)

        return Result.success()
    }

    private fun buildNotification(): Notification {
        val activityIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(context, DEFAULT_CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Timer value")
            .setContentText("$timerValue")
            .setNotificationSilent()
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel"
            val descriptionText = "default channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(DEFAULT_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}


