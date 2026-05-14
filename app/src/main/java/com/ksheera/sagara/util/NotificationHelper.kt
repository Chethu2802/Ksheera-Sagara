package com.ksheera.sagara.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.ksheera.sagara.MainActivity
import com.ksheera.sagara.R

object NotificationHelper {
    const val CHANNEL_ID = "ksheera_alerts"
    const val CHANNEL_NAME = "Ksheera Sagara Alerts"
    private const val CHANNEL_DESC = "Farm income, expense and reminder alerts"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = context.getSystemService(NotificationManager::class.java)
            if (mgr.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
                ).apply { description = CHANNEL_DESC }
                mgr.createNotificationChannel(channel)
            }
        }
    }

    fun show(context: Context, title: String, message: String, id: Int = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pi = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()

        val mgr = ContextCompat.getSystemService(context, NotificationManager::class.java) ?: return
        mgr.notify(id, notification)
    }
}
