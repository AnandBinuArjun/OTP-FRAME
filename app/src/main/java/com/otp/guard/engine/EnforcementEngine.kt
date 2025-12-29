package com.otp.guard.engine

import android.accessibilityservice.AccessibilityService
import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import com.otp.guard.OtpGuardApp
import com.otp.guard.ui.QuarantineActivity

object EnforcementEngine {
    private const val TAG = "EnforcementEngine"
    
    // In a real implementation, we would need access to the Service context
    var accessibilityService: AccessibilityService? = null

    fun enforce(packageName: String) {
        val context = OtpGuardApp.instance

        // 1. Kill Process
        killProcess(context, packageName)

        // 2. Clear Clipboard
        clearClipboard(context)

        // 3. Launch Quarantine UI
        launchQuarantineScreen(context, packageName)

        // 4. Revoke Permissions / Disable Accessibility
        // Ideally this involves automated interactions via our Accessibility Service 
        // to navigate to settings and revoke.
        disableMaliciousAppFeatures(packageName)
    }

    private fun killProcess(context: Context, packageName: String) {
        try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            am.killBackgroundProcesses(packageName)
            Log.d(TAG, "Attempted to kill process: $packageName")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to kill process", e)
        }
    }

    private fun clearClipboard(context: Context) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("", ""))
            Log.d(TAG, "Clipboard cleared.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear clipboard", e)
        }
    }

    private fun launchQuarantineScreen(context: Context, packageName: String) {
        val intent = Intent(context, QuarantineActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
            putExtra("EXTRA_PACKAGE_NAME", packageName)
        }
        context.startActivity(intent)
        Log.d(TAG, "Quarantine screen launched.")
    }

    private fun disableMaliciousAppFeatures(packageName: String) {
        accessibilityService?.let { service ->
             Log.d(TAG, "Using Accessibility Service to neutralize $packageName")
             // Implementation regarding system navigation would go here.
             // e.g. service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
        }
    }
}
