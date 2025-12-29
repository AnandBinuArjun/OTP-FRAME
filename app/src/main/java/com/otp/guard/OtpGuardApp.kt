package com.otp.guard

import android.app.Application

class OtpGuardApp : Application() {
    companion object {
        lateinit var instance: OtpGuardApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        com.otp.guard.security.DeviceIdentityManager.initializeIdentity()
    }
}
