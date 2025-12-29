package com.otp.guard.engine

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

object RiskCorrelationEngine {
    private const val TAG = "RiskCorrelationEngine"
    private const val CORRELATION_WINDOW_MS = 30_000L // 30 seconds

    // Map of PackageName to List of Timestamps of Suspicious Events
    private val appRiskLog = ConcurrentHashMap<String, MutableList<RiskEvent>>()

    fun reportSuspiciousActivity(packageName: String, eventType: RiskEventType, details: String) {
        Log.w(TAG, "Suspicious Activity Reported: $packageName - $eventType - $details")
        
        val currentTimestamp = System.currentTimeMillis()
        val events = appRiskLog.getOrPut(packageName) { mutableListOf() }
        
        // Add new event
        events.add(RiskEvent(currentTimestamp, eventType))

        // Clean up old events
        events.removeAll { it.timestamp < currentTimestamp - CORRELATION_WINDOW_MS }

        // Check for Correlation (2+ theft-class events in window)
        val recentEvents = events.filter { it.timestamp >= currentTimestamp - CORRELATION_WINDOW_MS }
        
        if (recentEvents.size >= 2) {
            triggerEnforcement(packageName)
        } else {
            Log.i(TAG, "Event logged for $packageName. Waiting for correlation.")
            // Sync single event to cloud for score adjustment
            com.otp.guard.network.CloudRiskSync.sendRiskEvent(packageName, eventType, details)
        }
    }

    private fun triggerEnforcement(packageName: String) {
        Log.e(TAG, "CRITICAL: THRESHOLD REACHED FOR $packageName. INITIATING ENFORCEMENT.")
        
        // Final "Death" Signal to Cloud
        com.otp.guard.network.CloudRiskSync.sendRiskEvent(packageName, RiskEventType.SCREEN_CAPTURE_ATTEMPT, "ENFORCEMENT_TRIGGERED")
        
        EnforcementEngine.enforce(packageName)
        // Clear log to prevent spamming
        appRiskLog.remove(packageName)
    }
}

data class RiskEvent(val timestamp: Long, val type: RiskEventType)

enum class RiskEventType {
    CLIPBOARD_ACCESS,
    ACCESSIBILITY_ABUSE,
    NOTIFICATION_INTERCEPTION,
    OVERLAY_INJECTION,
    SCREEN_CAPTURE_ATTEMPT
}
