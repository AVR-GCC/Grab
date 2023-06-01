package com.example.grab

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import java.net.ServerSocket
import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter

const val ServiceName = "Grab Provider 3"
const val Port = 6146

class ServerThread(private val logger: Logger, private val _nsdManager: NsdManager) : Thread() {
    private lateinit var nsdManager: NsdManager
    private var serverSocket: ServerSocket? = null
    private var isRunning = false

    fun startServer() {
        // Create an NSD service info object to advertise our service
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = ServiceName
            port = Port
            serviceType = "_http._tcp."
        }
        // Create an NSD service listener to handle events
        val nsdListener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(serviceInfo: NsdServiceInfo) {
                // Service was successfully registered
                logger.log("Service registered! Starting thread...")
                isRunning = true
                start()
            }
            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                // Registration failed
                logger.log("Regis tration failed! $errorCode")
            }
            override fun onServiceUnregistered(serviceInfo: NsdServiceInfo) {
                // Service was unregistered
                logger.log("Service unregistered")
            }
            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                // Unregistration failed
                logger.log("Service unregistration failed $errorCode")
            }
        }

        // Get an instance of the NsdManager and register our service
        nsdManager = _nsdManager
        logger.log("Registering service...")
        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, nsdListener)
    }

    override fun run() {
        try {
            serverSocket = ServerSocket(Port)
            while (isRunning) {
                val client: Socket? = serverSocket?.accept()
                client?.let {
                    val reader = BufferedReader(InputStreamReader(it.getInputStream()))
                    val writer = PrintWriter(it.getOutputStream(), true)
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        // Do something with the received message here
                        logger.log("Got line $line")
                        val number = line?.toIntOrNull()
                        if (number != null) {
                            logger.log("Received number: $number - Waiting")
                            sleep(1000) // wait for one second
                            val newNumber = number + 1
                            logger.log("Responding with $newNumber...")
                            writer.println(newNumber)
                        } else {
                            logger.log("Invalid number received: $line")
                        }
                    }
                    // Do something with the received message here
                    reader.close()
                    it.close()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopServer() {
        isRunning = false
        serverSocket?.close()
    }
}
