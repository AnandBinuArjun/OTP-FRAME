package com.otp.guard.network

import android.util.Log
import com.otp.guard.engine.RiskEventType
import com.otp.guard.security.DeviceIdentityManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

object CloudRiskSync {
    private const val TAG = "CloudRiskSync"
    private const val ENDPOINT = "https://api.otp-frame.com/v1/telemetry"

    fun sendRiskEvent(packageName: String, eventType: RiskEventType, details: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val payload = JSONObject().apply {
                    put("deviceId", DeviceIdentityManager.getPublicKey()) // Using PubKey as ID for demo
                    put("packageName", packageName)
                    put("eventType", eventType.name)
                    put("details", details)
                    put("timestamp", System.currentTimeMillis())
                }

                val signature = DeviceIdentityManager.signData(payload.toString())
                
                val finalEnvelope = JSONObject().apply {
                    put("payload", payload)
                    put("signature", signature)
                }

                // Mock Network Call
                Log.d(TAG, "TRANSMITTING RISK TELEMETRY TO CLOUD...")
                Log.d(TAG, "Payload: $finalEnvelope")
                
                // simulate latency
                kotlinx.coroutines.delay(200) 
                
                Log.d(TAG, "Cloud acknowledged risk. Fraud score updated.")
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync risk", e)
            }
        }
    }
}
