package com.otp.guard.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.otp.guard.R

class QuarantineActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quarantine)

        val pkgName = intent.getStringExtra("EXTRA_PACKAGE_NAME") ?: "Unknown"
        findViewById<TextView>(R.id.txtWarning).text = "Malicious activity detected from app: $pkgName"

        findViewById<Button>(R.id.btnUninstall).setOnClickListener {
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = Uri.parse("package:$pkgName")
            startActivity(intent)
        }

        // Lock Back Button
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing, block exit
            }
        })
    }
}
