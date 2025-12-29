package com.otp.guard.monitors

import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import com.otp.guard.engine.RiskCorrelationEngine
import com.otp.guard.engine.RiskEventType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ClipboardTheftMonitor(private val context: Context) {

    private val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    private val scope = CoroutineScope(Dispatchers.IO)
    private var monitoringJob: Job? = null

    private val clipListener = ClipboardManager.OnPrimaryClipChangedListener {
        checkClipboard()
    }

    fun start() {
        Log.d("ClipboardMonitor", "Starting Clipboard Monitor")
        clipboardManager.addPrimaryClipChangedListener(clipListener)
    }

    fun stop() {
        clipboardManager.removePrimaryClipChangedListener(clipListener)
        monitoringJob?.cancel()
    }

    private fun checkClipboard() {
        if (!clipboardManager.hasPrimaryClip()) return

        val clipData = clipboardManager.primaryClip ?: return
        if (clipData.itemCount > 0) {
            val text = clipData.getItemAt(0).text ?: return
            if (isOtpPattern(text.toString())) {
                Log.w("ClipboardMonitor", "OTP Detected in Clipboard! Starting 5s observation window.")
                startObservationWindow()
            }
        }
    }

    private fun isOtpPattern(text: String): Boolean {
        // Simple regex for 4-8 digits
        return text.matches(Regex("\\b\\d{4,8}\\b"))
    }

    private fun startObservationWindow() {
        // "tracks which non-whitelisted apps touch the clipboard... or trigger background execution"
        // In this simulation, we will scan UsageStats for any app that moved to foreground or showed activity recently
        
        monitoringJob?.cancel()
        monitoringJob = scope.launch {
            val endTime = System.currentTimeMillis() + 5000
            while (System.currentTimeMillis() < endTime) {
                checkForSuspiciousActivity()
                delay(500)
            }
        }
    }

    private fun checkForSuspiciousActivity() {
        // Implementation of UsageStatsManager check would go here
        // querying usage events for the last 1 second.
        // If a non-whitelisted app becomes active, report it.
        
        // Mock detection for the purpose of the engine
        // val suspiciousApp = findActiveApp()
        // if (suspiciousApp != null && !isWhitelisted(suspiciousApp)) {
        //     RiskCorrelationEngine.reportSuspiciousActivity(suspiciousApp, RiskEventType.CLIPBOARD_ACCESS, "Accessed clipboard during OTP window")
        // }
    }
}
