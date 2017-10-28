package com.example.bar.grab1;

import android.net.wifi.p2p.WifiP2pManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.app.Activity;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
//import android.support.design.widget.AppBarLayout;
//import android.support.design.widget.CollapsingToolbarLayout;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.Color;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GraberConsumer";
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 3;
    private static WifiManager wm;
    private static MainActivity self;
    private static List<android.net.wifi.ScanResult> netlist;
    private static TextView txtView;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static boolean searching;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;


    Intent pa;
    WifiManager wifi;
    LinearLayout ll;
    int size = 0;
    List<android.net.wifi.ScanResult> results;
    BroadcastReceiver wifiReciever;

    String ITEM_KEY = "key";
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.v(TAG, "granted");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.v(TAG, "not granted");
                }
                return;
            }


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(searching) unregisterReceiver(mReceiver);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        searching = false;
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new ConsumerListener(mManager, mChannel, this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        pa = new Intent(this, ProviderActivity.class);
        // Storage Permissions
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

/**
 * Checks if the app has permission to write to device storage
 *
 * If the app does not has permission then the user will be prompted to grant permissions
 *
 * @param activity
 */

        if (wifi.isWifiEnabled() == false) {
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(), "missing permissions", Toast.LENGTH_LONG).show();
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }
        Log.v(TAG, "enabled");
        WifiInfo wfi = wifi.getConnectionInfo();
        results = wifi.getScanResults();
        Log.v(TAG, "scanned");
        Button providerMenu = (Button) findViewById(R.id.pm);
        //Toast.makeText(getApplicationContext(), providerMenu.getText() + "#^$%#$%@$#@%#^%$&%^&^%", Toast.LENGTH_LONG).show();
        providerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(self, results.get(rg.getCheckedRadioButtonId() - 1).SSID, Toast.LENGTH_SHORT).show();
//                String message = editText.getText().toString();
//                intent.puProviderActivitytExtra(EXTRA_MESSAGE, message);

                self.startActivity(pa);
                Log.v(TAG, "activity started");
            }
        });
        Button refreshButton = (Button) findViewById(R.id.rf);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(self, results.get(rg.getCheckedRadioButtonId() - 1).SSID, Toast.LENGTH_SHORT).show();
                Toast.makeText(self, "refreshing...", Toast.LENGTH_SHORT).show();
                results = wifi.getScanResults();
                size = results.size();
                txtView = (TextView) findViewById(R.id.tv);
                String txt = "";
                for (int i = 0; i < size; i++) {
                    Log.v(TAG, "added" + results.get(i).SSID + "\n");
                    txt += results.get(i).SSID + "\n";
                }
                txtView.setText(txt);
                txtView.setVisibility(View.VISIBLE);
                //txtView.setTextColor(5);
                Log.v(TAG, "text set:\n" + txt);
            }
        });
        Button sendButton = (Button) findViewById(R.id.send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!searching) {
                    mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Log.v(TAG, "peers found!");
                        }

                        @Override
                        public void onFailure(int reasonCode) {
                            Log.v(TAG, "finding peers failed " + reasonCode);
                        }
                    });
                    registerReceiver(mReceiver, mIntentFilter);
                    searching = true;
                }
            }
        });
        sendButton.requestFocus();
        wifiReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                //-TODO - request permissions online:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "requesting permission");
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                    Log.v(TAG, "requested permission");
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

                } else {
                    results = wifi.getScanResults();
                    //do something, permission was previously granted; or legacy device
                    results = wifi.getScanResults();
                    size = results.size();
                    txtView = (TextView) findViewById(R.id.tv);
                    String txt = "";
                    for (int i = 0; i < size; i++) {
                        //Log.v(TAG, "added " + results.get(i).SSID);
                        txt += results.get(i).SSID + "\n";
                    }
                    txtView.setText(txt);
                    txtView.setVisibility(View.VISIBLE);
                }
            }
        };
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        self = this;


        arraylist.clear();
        Toast.makeText(this, "Scanning.... Please wait", Toast.LENGTH_SHORT).show();
        wifi.startScan();
    }
}

//android.support.design.widget.CollapsingToolbarLayout toolbar2 = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
//setSupportActionBar(toolbar);
//FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Log.v(TAG, "button Cricked!!!!!");
            }

        wm = (WifiManager) this.getSystemService(this.WIFI_SERVICE);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.registerReceiver(new MyReceiver(), new IntentFilter("android.net.wifi."));
        Log.v(TAG, "super onCreated!!!!!");
        //LinearLayout wrapper = (LinearLayout) inflater.inflate(R.layout.content_currier, null);
        //LinearLayout wrapper = (LinearLayout) findViewById(R.layout.content_currier);
        rg = (RadioGroup) findViewById(R.id.RBWrapper);
        //RadioButton button;
        //WifiInfo wfinfor = wm.getConnectionInfo();
        wm.startScan();




        this.invalidateOptionsMenu();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_curreir, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public class MyReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            netlist = wm.getScanResults();

            for(int i = 0; i < 3; i++) {
                RadioButton button = new RadioButton(self);
                button.setText("RB: " + i);
                rg.addView(button);

            }
            rg.invalidate();
        }
    }
}
public class WiFiDemo extends Activity implements OnClickListener
{
    WifiManager wifi;
    ListView lv;
    TextView textStatus;
    Button buttonScan;
    int size = 0;
    List<android.net.wifi.ScanResult> results;

    String ITEM_KEY = "key";
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;

    /* Called when the activity is first created. *//*
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);

        //textStatus = (TextView) findViewById(R.id.textStatus);
        //buttonScan = (Button) findViewById(R.id.buttonScan);
        //buttonScan.setOnClickListener(this);
        //lv = (ListView)findViewById(R.id.list);


    }

    public void onClick(View view)
    {

    }
}*/