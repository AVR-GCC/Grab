import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import com.example.grab.Logger
import java.net.Socket
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress

const val ServiceName = "Grab Provider 3"

class ClientThread(private val logger: Logger, private val _nsdManager: NsdManager) : Thread() {
    private lateinit var nsdManager: NsdManager
    private var port: Int = 0
    private var host: InetAddress? = null
    private lateinit var socket: Socket
    private var isRunning = false

    fun startClient() {
        // Create an NSD discovery listener to handle events
        val discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(serviceType: String) {
                // Discovery started
                logger.log("Discovering...")
            }
            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                logger.log("Service found: ${serviceInfo.serviceName}")
                if (serviceInfo.serviceName == ServiceName) {
                    // Service found, resolve it to get its IP address and port
                    nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                            // Resolve failed
                        }
                        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                            // Service resolved, get its IP address and port
                            host = serviceInfo.host
                            port = serviceInfo.port
                            // Use the IP address and port to establish a socket connection to the server
                            isRunning = true
                            start()
                        }
                    })
                }
            }
            override fun onDiscoveryStopped(serviceType: String) {
                // Discovery stopped
                logger.log("Discovery stopped")
            }
            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                // Service lost
                logger.log("Service lost")
            }
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                // Start discovery failed
                logger.log("Start discover failed $errorCode")
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                // Stop discovery failed
                logger.log("Stop discovery failed $errorCode")
            }
        }

        logger.log("Discovering hosts...")
        nsdManager = _nsdManager
        nsdManager.discoverServices("_http._tcp.", NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    override fun run() {
        try {
            logger.log("Establishing socket: $host - $port")
            socket = Socket(host, port)
            val writer = PrintWriter(socket.getOutputStream(), true)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            var number = 1
            while (isRunning) {
                logger.log("Sending number: $number")
                writer.println(number)
                val line = reader.readLine()
                val receivedNumber = line?.toIntOrNull()
                if (receivedNumber != null) {
                    logger.log("Received number: $receivedNumber")
                    sleep(1000) // wait for one second
                    number = receivedNumber + 1
                } else {
                    logger.log("Invalid number received: $line")
                    break
                }
            }
            writer.close()
            reader.close()
            socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopClient() {
        isRunning = false
    }
}
