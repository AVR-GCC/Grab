package com.example.bar.grab1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by bar on 6/4/17.
 */

public class ProviderActivity extends AppCompatActivity {
    Intent ca;
    private static final String TAG = "GraberProvider";
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    LinearLayout fp;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ca = new Intent(this, MainActivity.class);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new ProviderListener(mManager, mChannel, this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        Button consumerMenu = (Button) findViewById(R.id.consumer_menu);
        consumerMenu.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                //Toast.makeText(self, results.get(rg.getCheckedRadioButtonId() - 1).SSID, Toast.LENGTH_SHORT).show();
//                String message = editText.getText().toString();
//                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(ca);

            }
        });
        Button saveButton = (Button) findViewById(R.id.save1);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(self, results.get(rg.getCheckedRadioButtonId() - 1).SSID, Toast.LENGTH_SHORT).show();
//                String message = editText.getText().toString();
//                intent.putExtra(EXTRA_MESSAGE, message);

                registerReceiver(mReceiver, mIntentFilter);
                Log.v(TAG, "reciever registered");

            }
        });
        saveButton.requestFocus();

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.v(TAG, "peers found!");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.v(TAG, "peer search failed: " + reasonCode);
            }
        });


    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
