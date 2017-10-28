package com.example.bar.grab1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by bar on 6/9/17.
 */

public class ConsumerListener extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;


    private WifiP2pDevice thedev;
    private WifiP2pConfig config;

    private boolean connected;
    private boolean socketOpen;
    private Thread connector;
    private static final String TAG = "GraberConsumerListener";
    WifiP2pManager.PeerListListener myPeerListListener;
    WifiP2pManager.ConnectionInfoListener connectInfo;

    ServerSocket serverSocket;
    Context context;
    String host;
    int port;
    int len;
    Socket hostsock = new Socket();
    byte buf[]  = new byte[1024];

    public ConsumerListener() {
        super();
    }

    public ConsumerListener(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity) {

        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;

        context = activity.getApplicationContext();
        port = 8888;
        host = "grabhost";
        serverSocket = null;

        myPeerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                Log.v(TAG, "in peerlistlistener");
                if(!connected) {
                    Log.v(TAG, "first connection attempt, after recieving peerlist in listener");
                    thedev = (WifiP2pDevice) peers.getDeviceList().toArray()[0];
                    config = new WifiP2pConfig();
                    config.deviceAddress = thedev.deviceAddress;
                    mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Log.v(TAG, "connection succeeded to: " + config.deviceAddress + "... getting connection info...");

                            //connector.run();
                        }

                        @Override
                        public void onFailure(int reasonCode) {
                            Log.v(TAG, "connection failed :(: " + reasonCode + " in the onFail");
                        }
                    });
                }
                else {
//                    try {
//                        Log.v(TAG, "connected already... sending again");
//                        OutputStream outputStream = socket.getOutputStream();
//                        len = 13;
//                        outputStream.write("Yo Graber2!!!".getBytes(), 0, 13);
//                        outputStream.close();
//
//                    } catch (Exception e) {
//                        Log.e(TAG, e.getMessage() + "#$%#^^$&%^&%^&$%^%$^");
//                    }
                }
            }
        };
        connector = new Thread(new Runnable() {
            @Override
            public void run() {
                /**
                 * Create a server socket and wait for client connections. This
                 * call blocks until a connection is accepted from a client
                 */
                try {
                    serverSocket = new ServerSocket(8558);
                    hostsock = serverSocket.accept();
                    Log.v(TAG, "client found: " + hostsock.toString());
                    InputStream instream = hostsock.getInputStream();
                    Log.v(TAG, "with instream" + instream.toString());
                    serverSocket.close();
                    Log.v(TAG, "server sock closed");
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage() + " in the connector@#@@@");
                }
            }
        });
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
            } else {
                // Wi-Fi P2P is not enabled
            }
        }

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            //WifiP2pManager.requestPeers(this.mChannel, this.mActivity);
            Log.v(TAG, "peers changed! in the onRecieve");
            if (mManager != null) {
                Log.v(TAG, "requesting new peerlist");
                mManager.requestPeers(mChannel, myPeerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            Log.v(TAG, "connections changed!");
            mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(WifiP2pInfo info) {
                    Log.v(TAG, "isConnected " + info.groupFormed + "groupOwnerAddress: " + info.groupOwnerAddress);
                    connector.run();
                }
            });
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
            Log.v(TAG, "this device changed @ onReccieve");
        }
    }
}
