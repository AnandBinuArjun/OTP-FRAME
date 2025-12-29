package com.otp.guard.monitors

import android.content.Context
import android.hardware.display.DisplayManager
import android.util.Log
import com.otp.guard.engine.RiskCorrelationEngine
import com.otp.guard.engine.RiskEventType

class ScreenCaptureMonitor(private val context: Context) {

    private val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) {
            checkForScreenCabture()
        }

        override fun onDisplayRemoved(displayId: Int) {}
        override fun onDisplayChanged(displayId: Int) {}
    }

    fun start() {
        displayManager.registerDisplayListener(displayListener, null)
    }

    fun stop() {
        displayManager.unregisterDisplayListener(displayListener)
    }

    private fun checkForScreenCabture() {
        val displays = displayManager.displays
        for (display in displays) {
            // Check flags. FLAG_SECURE prevents it, but we want to detect the *attempt* or existence of virtual display
            if (display.displayId != 0) { // Main display is 0
                // Heuristic: Additional displays often mean casting or recording
                // Check display name or type if possible
                Log.w("ScreenCapture", "Secondary display detected: ${display.name}")
                
                 RiskCorrelationEngine.reportSuspiciousActivity(
                    "Unknown(Display)", 
                    RiskEventType.SCREEN_CAPTURE_ATTEMPT, 
                    "Secondary display detected (Screen Casting/Recording)"
                )
            }
        }
    }
}
