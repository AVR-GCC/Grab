package com.example.grab

import ClientThread
import android.Manifest
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.net.Socket

class MainActivity : AppCompatActivity(), Logger {
    private lateinit var logger: Logger
    private lateinit var tvMessage: TextView
    private lateinit var nsdManager: NsdManager
    private var logHandler = Handler(Looper.getMainLooper())
    private lateinit var wifiManager: WifiManager
    private var neededPermissions:Array<String> = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.INTERNET
    );

    private val REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//        if (wifiManager.isWifiEnabled) {
//            wifiManager.isWifiEnabled = false
//        }
//
//        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        requestPermissions(getPermissionsToRequest())

        var btnConnect = findViewById<Button>(R.id.btnConnect)
        var btnHost = findViewById<Button>(R.id.btnHost)
        tvMessage = findViewById<TextView>(R.id.tvMessage)
        nsdManager = getSystemService(Context.NSD_SERVICE) as NsdManager


//        var myScanReceiver = MyScanReceiver(wifiManager) {
//            try {
//                val myNetworksList = wifiManager.scanResults
//                println("Got list $myNetworksList")
//                for (result in myNetworksList) {
//                    println("Wi-Fi Network - SSID: ${result.SSID}, BSSID: ${result.BSSID}, Signal Strength: ${result.level}")
//                }
//            } catch (e: SecurityException) {
//                println("No permission: scanResults")
//            }
//        }
//
//        val intentFilter = IntentFilter()
//        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
//        registerReceiver(myScanReceiver, intentFilter)

//        btnButton.setOnClickListener {
//            println("-=-=-=-=-=-=-=-=-=-=-=-")
//            try {
//                println("Starting...")
//                wifiManager.startScan()
//                println("Started!")
//            } catch (e: SecurityException) {
//                println("No permission: startScan")
//            }
//        }
        btnHost.setOnClickListener {
            var serverThread = ServerThread(this, nsdManager);
            serverThread.startServer()
            logHandler.post {
                btnHost.text = "Stop"
            }
            btnHost.setOnClickListener {
                serverThread.stopServer()
                this.log("Server stopped")
            }
        }

        btnConnect.setOnClickListener {
            var clientThread = ClientThread(this, nsdManager);
            clientThread.startClient()
        }
    }

    override fun log(text: String) {
        runOnUiThread {
            tvMessage.text = text
        }
    }

    private fun getPermissionsToRequest(): Array<String> {
        var missingPermissions: Array<String> = arrayOf();
        for (permission in neededPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.plus(permission);
            }
        }
        return missingPermissions
    }

    private fun requestPermissions(missingPermissions: Array<String>) {
        if (missingPermissions.isEmpty()) return
        ActivityCompat.requestPermissions(this, missingPermissions, REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                println("Permissions granted!")
            } else {
                println("Permissions refused.")
            }
        }
    }
}