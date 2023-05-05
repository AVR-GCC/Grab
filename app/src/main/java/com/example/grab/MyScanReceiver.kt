package com.example.grab

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager

class MyScanReceiver(private val wifiManager: WifiManager, private val callback: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        println("MyScanReceiver got action ${intent?.action}")
        if (intent?.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
            // The Wi-Fi scan is complete
            println("MyScanReceiver invoking callback")
            callback.invoke()
        }
    }
}
