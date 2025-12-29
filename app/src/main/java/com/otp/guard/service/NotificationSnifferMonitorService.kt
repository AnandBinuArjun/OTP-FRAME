package com.otp.guard.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.otp.guard.engine.RiskCorrelationEngine
import com.otp.guard.engine.RiskEventType

class NotificationSnifferMonitorService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.let {
            val text = it.notification.extras.getCharSequence(android.app.Notification.EXTRA_TEXT)?.toString() ?: ""
            val title = it.notification.extras.getCharSequence(android.app.Notification.EXTRA_TITLE)?.toString() ?: ""
            
            if (isOtpNotification(title, text)) {
                // Check who else is listening?
                // In reality, we can't easily query *other* listeners from here without higher privileges or dumpsys
                // But the prompt says "it checks which apps have NotificationListener privileges"
                // This implies a check against PackageManager for apps requesting the permission.
                
                checkForPotentialSniffers()
            }
        }
    }

    private fun isOtpNotification(title: String, text: String): Boolean {
        val combined = "$title $text".lowercase()
        return combined.contains("otp") || combined.contains("code") || combined.matches(Regex(".*\\d{4,6}.*"))
    }

    private fun checkForPotentialSniffers() {
        // Mock logic: Identify apps that are NOT system and have permission
        // List packages with enabled permission
        // If found, report to engine
        
        // Simulating a finding for demonstration
        // RiskCorrelationEngine.reportSuspiciousActivity("com.malware.sim", RiskEventType.NOTIFICATION_INTERCEPTION, "Active listener during OTP receipt")
        // This would be fleshed out with PackageManager logic
    }
}
