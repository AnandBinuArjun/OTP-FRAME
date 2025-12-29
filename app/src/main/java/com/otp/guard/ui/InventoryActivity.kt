package com.otp.guard.ui

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.otp.guard.R

class InventoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        val recycler = findViewById<RecyclerView>(R.id.recyclerInventory)
        recycler.layoutManager = LinearLayoutManager(this)
        
        val apps = getInstalledApps()
        recycler.adapter = InventoryAdapter(apps)
    }

    private fun getInstalledApps(): List<AppItem> {
        val pm = packageManager
        return pm.getInstalledTopAppPackages().map {
            AppItem(
                it.loadLabel(pm).toString(),
                it.packageName,
                it.loadIcon(pm),
                isWhitelisted(it.packageName)
            )
        }
    }

    private fun PackageManager.getInstalledTopAppPackages(): List<ApplicationInfo> {
        return getInstalledPackages(0)
            .map { it.applicationInfo }
            .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 } // Filter out system apps
    }

    private fun isWhitelisted(pkg: String): Boolean {
        // Mock whitelist logic
        return pkg.contains("bank") || pkg.contains("wallet")
    }

    data class AppItem(val name: String, val pkg: String, val icon: Drawable, var isWhitelisted: Boolean)

    inner class InventoryAdapter(private val list: List<AppItem>) : RecyclerView.Adapter<InventoryAdapter.Holder>() {
        
        inner class Holder(v: View) : RecyclerView.ViewHolder(v) {
            val icon: ImageView = v.findViewById(R.id.appIcon)
            val name: TextView = v.findViewById(R.id.appName)
            val pkg: TextView = v.findViewById(R.id.pkgName)
            val check: CheckBox = v.findViewById(R.id.checkWhitelist)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_inventory_app, parent, false))
        }

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val item = list[position]
            holder.name.text = item.name
            holder.pkg.text = item.pkg
            holder.icon.setImageDrawable(item.icon)
            holder.check.isChecked = item.isWhitelisted
            
            holder.check.setOnCheckedChangeListener { _, isChecked -> 
                item.isWhitelisted = isChecked
                // In real app, save to SharedPreferences or DB
            }
        }
    }
}
