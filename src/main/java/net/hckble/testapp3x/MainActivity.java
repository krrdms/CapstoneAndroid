package net.hckble.testapp3x;

import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import net.hckble.testapp3x.model.AccessPoint;

public class MainActivity extends AppCompatActivity{

    private WifiManager wifiManager;
    private ListView listView, listViewPub;
    private Button buttonScan;
    private int size = 0;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<String> arrayListPub = new ArrayList<>();
    private ArrayList<AccessPoint> arrayListAP = new ArrayList<>();
    private ArrayAdapter adapter, adapterPub;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonScan = findViewById(R.id.scanBtn);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanWifi();
            }
        });
        //Intent myIntent = new Intent (Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        //startActivity(myIntent);

        listViewPub = findViewById(R.id.wifiListPub);
        listView = findViewById(R.id.wifiList);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        } else Toast.makeText(this, "WiFi is enabled", Toast.LENGTH_LONG).show();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        adapterPub = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListPub);
        listView.setAdapter(adapter);
        listViewPub.setAdapter(adapterPub);
        listViewPub.setBackgroundColor(Color.GRAY);
    }

    private void scanWifi() {
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.EXTRA_RESULTS_UPDATED));
        if (wifiManager.startScan())
            Toast.makeText(this, "Trying Wifi Scan...", Toast.LENGTH_SHORT).show();
        results = wifiManager.getScanResults();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(
                    WifiManager.EXTRA_RESULTS_UPDATED, true);
            scanSuccess(context, intent);
        }

        public void scanSuccess(Context context, Intent intent) {
            AccessPoint ap;
            results = wifiManager.getScanResults();
            unregisterReceiver(this);

            arrayListAP.clear();
            arrayList.clear();
            arrayListPub.clear();
            adapter.notifyDataSetChanged();
            adapterPub.notifyDataSetChanged();

            for (ScanResult scanResult : results) {
                ap = new AccessPoint(scanResult.BSSID, scanResult.SSID, scanResult.capabilities);
                arrayListAP.add(ap);
                if (ap.isEncrypted())
                    arrayList.add("SSID:" + ap.getSSID() + " - BSSID:" + ap.getBSSID() + " - Encrypted:" + ap.isEncrypted());
                else
                    arrayListPub.add("SSID:" + ap.getSSID() + " - BSSID:" + ap.getBSSID() + " - Encrypted:" + ap.isEncrypted());
            adapter.notifyDataSetChanged();
            adapterPub.notifyDataSetChanged();
            }
        }

        public void checkAccessPointOUI(AccessPoint ap) {

        }
    };
}