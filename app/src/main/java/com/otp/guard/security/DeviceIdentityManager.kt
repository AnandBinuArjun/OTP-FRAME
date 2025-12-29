package com.otp.guard.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.Signature

object DeviceIdentityManager {
    private const val KEY_ALIAS = "OtpFrameDeviceIdentity"
    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val TAG = "DeviceIdentity"

    fun initializeIdentity() {
        try {
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
            keyStore.load(null)

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                Log.i(TAG, "Generating new hardware-backed identity...")
                val kpg = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_EC,
                    KEYSTORE_PROVIDER
                )
                val parameterSpec = KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
                ).run {
                    setDigests(KeyProperties.DIGEST_SHA256)
                    setIsStrongBoxBacked(false) // Try true on real devices with StrongBox
                    build()
                }
                
                kpg.initialize(parameterSpec)
                kpg.generateKeyPair()
                Log.i(TAG, "Identity generated successfully.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to init identity", e)
        }
    }

    fun signData(data: String): String {
        return try {
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
            keyStore.load(null)
            val entry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.PrivateKeyEntry ?: return ""
            
            val signature = Signature.getInstance("SHA256withECDSA")
            signature.initSign(entry.privateKey)
            signature.update(data.toByteArray())
            
            Base64.encodeToString(signature.sign(), Base64.NO_WRAP)
        } catch (e: Exception) {
            Log.e(TAG, "Signing failed", e)
            ""
        }
    }

    fun getPublicKey(): String {
        return try {
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
            keyStore.load(null)
            val cert = keyStore.getCertificate(KEY_ALIAS)
            Base64.encodeToString(cert.publicKey.encoded, Base64.NO_WRAP)
        } catch (e: Exception) {
            ""
        }
    }
}
