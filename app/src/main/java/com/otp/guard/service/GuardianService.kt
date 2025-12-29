package com.otp.guard.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.otp.guard.R
import com.otp.guard.monitors.ClipboardTheftMonitor
import com.otp.guard.monitors.OverlayAttackMonitor
import com.otp.guard.monitors.ScreenCaptureMonitor

class GuardianService : Service() {

    private lateinit var clipboardMonitor: ClipboardTheftMonitor
    private lateinit var overlayMonitor: OverlayAttackMonitor
    private lateinit var screenCaptureMonitor: ScreenCaptureMonitor

    override fun onCreate() {
        super.onCreate()
        Log.d("GuardianService", "Starting Guardian Service...")
        startForeground(1, createNotification())

        clipboardMonitor = ClipboardTheftMonitor(this)
        overlayMonitor = OverlayAttackMonitor(this)
        screenCaptureMonitor = ScreenCaptureMonitor(this)

        clipboardMonitor.start()
        overlayMonitor.start()
        screenCaptureMonitor.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        clipboardMonitor.stop()
        overlayMonitor.stop()
        screenCaptureMonitor.stop()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(): Notification {
        val channelId = "GuardianChannel"
        val channelName = "Guardian Service"
        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(chan)

        return Notification.Builder(this, channelId)
            .setContentTitle("OTP Guard Active")
            .setContentText("Monitoring for theft attempts and malware.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }
}
