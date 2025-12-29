package com.otp.guard.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.otp.guard.engine.EnforcementEngine
import com.otp.guard.engine.RiskCorrelationEngine
import com.otp.guard.engine.RiskEventType

class AccessibilityAbuseMonitorService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("AccessibilityAbuse", "Accessibility Service Connected")
        EnforcementEngine.accessibilityService = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            checkForAbuse(it)
        }
    }

    override fun onInterrupt() {
        Log.d("AccessibilityAbuse", "Accessibility Service Interrupted")
    }

    private fun checkForAbuse(event: AccessibilityEvent) {
        // Logic: specific keywords like "OTP", "verification code"
        // Also check if triggers auto-click events while banking app in foreground
        
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            
            val text = event.text.toString()
            if (isSensitiveContext(text)) {
                val packageName = event.packageName?.toString() ?: return
                // Filter out system apps or self
                if (packageName != packageName) { // simplified check, assume whitelist check here
                    Log.w("AccessibilityAbuse", "Sensitive text detected read by: $packageName")
                     RiskCorrelationEngine.reportSuspiciousActivity(
                        packageName,
                        RiskEventType.ACCESSIBILITY_ABUSE,
                        "Read sensitive text: $text"
                    )
                }
            }
        }
    }

    private fun isSensitiveContext(text: String): Boolean {
        val lower = text.lowercase()
        return lower.contains("otp") || 
               lower.contains("verification code") || 
               lower.matches(Regex(".*\\b\\d{4,8}\\b.*"))
    }
}
