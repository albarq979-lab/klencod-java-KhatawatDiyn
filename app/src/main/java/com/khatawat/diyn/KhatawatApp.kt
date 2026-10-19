package com.khatawat.diyn

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.khatawat.diyn.notification.NotificationHelper

class KhatawatApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            NotificationHelper.CHANNEL_DEBTS,
            getString(R.string.channel_debts_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = getString(R.string.channel_debts_desc)
        }
        nm.createNotificationChannel(channel)
    }
}
