package com.example.bar.grab1;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by bar on 6/7/17.
 */

public class ProviderListener extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private ProviderActivity mActivity;
    private WifiP2pDevice thedev;
    private WifiP2pConfig config;

    private boolean connected;
    private boolean socketOpen;
    private Thread connector;
    private static final String TAG = "GraberProviderListener";
    WifiP2pManager.PeerListListener myPeerListListener;
    WifiP2pManager.ConnectionInfoListener connectInfo;
    InetSocketAddress isa;

    Context context;
    String host;
    int port;
    int len;
    Socket socket = new Socket();
    byte buf[]  = new byte[1024];

    public ProviderListener() {
        super();
    }

    public ProviderListener(WifiP2pManager manager, WifiP2pManager.Channel channel, ProviderActivity activity) {

        super();
        connected = false;
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
        context = activity.getApplicationContext();


        connector = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /**
                     * Create a client socket with the host,
                     * port, and timeout information.
                     */

                    socket.bind(null);
                    //port = 8888;
                    //host = "192.168.49.1";
                    //Toast.makeText(context, "connecting...", Toast.LENGTH_LONG).show();
                    Log.v(TAG, "connecting...");
                    socket.connect(isa, 20000);
                    Log.v(TAG, "server found on channel: " + socket.getChannel() + " local address: " + socket.getLocalAddress());
                    connected = true;
                    //Toast.makeText(context, "server found on channel: " + socket.getChannel() + " local address: " + socket.getLocalAddress(), Toast.LENGTH_LONG).show();
                    OutputStream outputStream = socket.getOutputStream();
                    len = 13;
                    outputStream.write("Yo Graber!!!".getBytes(), 0, 13);
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    Log.e(TAG, e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage() + "in the catch!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    //connected = false;
                }
            }
        });

        myPeerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                Log.v(TAG, "in peerlistlistener");
                if(!connected) {
                    Log.v(TAG, "first connection attempt, after recieving peerlist in listener");

//                    thedev = (WifiP2pDevice) peers.getDeviceList().toArray()[0];
//                    config = new WifiP2pConfig();
//                    config.deviceAddress = thedev.deviceAddress;
//                    mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
//                        @Override
//                        public void onSuccess() {
//                            Log.v(TAG, "connection succeeded to: " + config.deviceAddress + "... running connector");
//                            connector.run();
//                        }
//
//                        @Override
//                        public void onFailure(int reasonCode) {
//                            Log.v(TAG, "connection failed :(: " + reasonCode + " in the onFail");
//                        }
//                    });
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
    }//constructor



    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.v(TAG, "wifi P2P enabled.");
            } else {
                Log.v(TAG, "wifi P2P not enabled.");
            }
        }

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            //WifiP2pManager.requestPeers(this.mChannel, this.mActivity);
            Log.v(TAG, "peers changed! in provider onrecieve");
            if (mManager != null) {
                mManager.requestPeers(mChannel, myPeerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            Log.v(TAG, "connections changed!" + connected);
            mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(WifiP2pInfo info) {
                    Log.v(TAG, "groupOwnerAddress: " + info.groupOwnerAddress);
                    isa = new InetSocketAddress(info.groupOwnerAddress, 8558);
                    connector.run();
                }
            });
            if(connected) {

            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }

    public File getTempFile(Context context, String url) {
        File file = null;
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
        } catch (IOException e) {
            // Error while creating file
        }
        return file;
    }
}

