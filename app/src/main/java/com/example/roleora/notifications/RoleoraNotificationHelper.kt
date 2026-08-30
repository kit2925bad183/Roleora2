package com.example.roleora.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity

class RoleoraNotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_REMINDERS = "roleora_reminders"
        const val CHANNEL_TIMER = "roleora_timer"
        const val CHANNEL_SYNC = "roleora_sync"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            val remindersChannel = NotificationChannel(
                CHANNEL_REMINDERS,
                "Task & Event Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for scheduled tasks, calendar events, and reminders"
                enableVibration(true)
            }

            val timerChannel = NotificationChannel(
                CHANNEL_TIMER,
                "Work Session Timer",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Active focus session and timer status"
            }

            val syncChannel = NotificationChannel(
                CHANNEL_SYNC,
                "Sync & Storage Status",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for offline sync and background uploads"
            }

            notificationManager.createNotificationChannels(listOf(remindersChannel, timerChannel, syncChannel))
        }
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun showReminderNotification(
        notificationId: Int,
        title: String,
        message: String,
        isConfidential: Boolean = false
    ) {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val displayTitle = if (isConfidential) "Roleora Reminder" else title
        val displayText = if (isConfidential) "You have a scheduled reminder" else message

        val notification = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(displayTitle)
            .setContentText(displayText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVisibility(if (isConfidential) NotificationCompat.VISIBILITY_PRIVATE else NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (ignored: SecurityException) {}
    }

    fun showTimerNotification(
        notificationId: Int = 9001,
        title: String,
        elapsedText: String
    ) {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_TIMER)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(title)
            .setContentText(elapsedText)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (ignored: SecurityException) {}
    }

    fun cancelNotification(notificationId: Int) {
        try {
            NotificationManagerCompat.from(context).cancel(notificationId)
        } catch (ignored: Exception) {}
    }
}
