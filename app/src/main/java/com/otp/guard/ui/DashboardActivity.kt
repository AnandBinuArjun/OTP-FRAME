package com.otp.guard.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.otp.guard.R
import com.otp.guard.service.GuardianService

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Start Core Foreground Service
        startForegroundService(Intent(this, GuardianService::class.java))

        setupUI()
        checkPermissionsAndStatus()
    }

    private fun setupUI() {
        // Inventory Card
        findViewById<View>(R.id.cardInventory).setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java))
        }

        // Whitelist Card
        findViewById<View>(R.id.cardWhitelist).setOnClickListener {
            Toast.makeText(this, "Opening Whitelist...", Toast.LENGTH_SHORT).show()
        }

        // Setup Monitor Cards
        configureMonitorCard(R.id.monitorClipboard, "Clipboard Monitor", "Watching for Copied OTPs")
        configureMonitorCard(R.id.monitorAccessibility, "Accessibility Monitor", "Detecting Text Scraping")
        configureMonitorCard(R.id.monitorNotification, "Notification Monitor", "Securing SMS Inputs")
        configureMonitorCard(R.id.monitorOverlay, "Overlay Guard", "Preventing Fake Login UIs")
        configureMonitorCard(R.id.monitorScreen, "Screen Shield", "Blocking Screen Recorders")
    }

    private fun configureMonitorCard(cardId: Int, name: String, desc: String) {
        val card = findViewById<View>(cardId)
        card.findViewById<TextView>(R.id.title).text = name
        card.findViewById<TextView>(R.id.subtitle).text = desc
    }

    private fun checkPermissionsAndStatus() {
        // In a real app, we would dynamically update the "ACTIVE" chips based on 
        // whether the specific permission is granted or the service is running.
        
        if (!Settings.canDrawOverlays(this)) {
            // Demo: Show Overlay Monitor as WARNING
            updateMonitorStatus(R.id.monitorOverlay, "NEEDS PERM", false)
            // Trigger prompt flow...
        }
    }

    private fun updateMonitorStatus(cardId: Int, statusText: String, isActive: Boolean) {
        val card = findViewById<View>(cardId)
        val statusView = card.findViewById<TextView>(R.id.status)
        statusView.text = statusText
        
        if (!isActive) {
           // We would change the drawable background tint here to @color/status_warning
           // For now, text update suffices for the demo structure
           statusView.setTextColor(resources.getColor(R.color.status_warning, theme))
        }
    }
}
