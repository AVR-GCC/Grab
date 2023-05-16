package com.example.grab

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


class MainActivity : AppCompatActivity() {
    private lateinit var nsdManager: NsdManager
    private var nsdHandler = Handler(Looper.getMainLooper())
    private lateinit var wifiManager: WifiManager
    private lateinit var wifiP2PManager: WifiP2pManager
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
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = false
        }

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        requestPermissions(getPermissionsToRequest())

        var btnConnect = findViewById<Button>(R.id.btnConnect)
        var btnHost = findViewById<Button>(R.id.btnHost)
        var tvMessage = findViewById<TextView>(R.id.tvMessage)

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
            // Create an NSD service info object to advertise our service
            val serviceInfo = NsdServiceInfo().apply {
                serviceName = "Grab Provider 2"
                serviceType = "_http._tcp."
                port = 8080
            }

            // Create an NSD service listener to handle events
            val nsdListener = object : NsdManager.RegistrationListener {
                override fun onServiceRegistered(serviceInfo: NsdServiceInfo) {
                    // Service was successfully registered
                    nsdHandler.post {
                        tvMessage.text = "Service registered! Starting thread..."
                    }
                    // Start a new thread to accept incoming connections
                    var serverThread = ServerThread(serviceInfo.port)
                    serverThread.startServer()
                }
                override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                    // Registration failed
                    nsdHandler.post {
                        tvMessage.text = "Registration failed! $errorCode"
                    }
                }
                override fun onServiceUnregistered(serviceInfo: NsdServiceInfo) {
                    // Service was unregistered
                    nsdHandler.post {
                        tvMessage.text = "Service unregistered"
                    }
                }
                override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                    // Unregistration failed
                    nsdHandler.post {
                        tvMessage.text = "Service unregistration failed $errorCode"
                    }
                }
            }

            // Get an instance of the NsdManager and register our service
            nsdManager = getSystemService(Context.NSD_SERVICE) as NsdManager
            tvMessage.text = "Registering service..."
            nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, nsdListener)
        }

        btnConnect.setOnClickListener {
            // Create an NSD discovery listener to handle events
            val discoveryListener = object : NsdManager.DiscoveryListener {
                override fun onDiscoveryStarted(serviceType: String) {
                    // Discovery started
                    nsdHandler.post {
                        tvMessage.text = "Discovering..."
                    }
                }
                override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                    nsdHandler.post {
                        tvMessage.text = "Service found: ${serviceInfo.serviceName}"
                    }
                    if (serviceInfo.serviceName == "Grab Provider 2") {
                        // Service found, resolve it to get its IP address and port
                        nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                                // Resolve failed
                            }
                            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                                // Service resolved, get its IP address and port
                                val host = serviceInfo.host
                                val port = serviceInfo.port
                                // Use the IP address and port to establish a socket connection to the server
                                nsdHandler.post {
                                    tvMessage.text = "Establishing socket: $host - $port"
                                }
                                try {
                                    val socket = Socket(host, port)
                                } catch (e: IOException) {
                                    nsdHandler.post {
                                        tvMessage.text = "Error with socket: $e"
                                    }
                                }
                            }
                        })
                    }
                }
                override fun onDiscoveryStopped(serviceType: String) {
                    // Discovery stopped
                    nsdHandler.post {
                        tvMessage.text = "Discovery stopped"
                    }
                }
                override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                    // Service lost
                    nsdHandler.post {
                        tvMessage.text = "Service lost"
                    }
                }
                override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                    // Start discovery failed
                    nsdHandler.post {
                        tvMessage.text = "Start discover failed $errorCode"
                    }
                }

                override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                    // Stop discovery failed
                    nsdHandler.post {
                        tvMessage.text = "Stop discovery failed $errorCode"
                    }
                }
            }

            // Get an instance of the NsdManager and start the discovery process
            nsdManager = getSystemService(Context.NSD_SERVICE) as NsdManager
            tvMessage.text = "Discovering hosts..."
            nsdManager.discoverServices("_http._tcp.", NsdManager.PROTOCOL_DNS_SD, discoveryListener)
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