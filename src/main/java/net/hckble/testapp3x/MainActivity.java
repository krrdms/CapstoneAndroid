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

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import net.hckble.testapp3x.model.AccessPoint;
import net.hckble.testapp3x.model.OUI;
import net.hckble.testapp3x.model.OUIqry;
import net.hckble.testapp3x.network.GetOIUDataService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *  Android main class - JKA
 */

public class MainActivity extends AppCompatActivity{

    private WifiManager wifiManager;
    private ListView listView, listViewPub;
    private Button buttonScan;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<String> arrayListPub = new ArrayList<>();
    private ArrayAdapter adapter, adapterPub;

    private static final String BASE_URL = "https://pop.hckbl.net/";

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

        if (!wifiManager.isWifiEnabled()) { /* may want to tell user we're toggling wifi setting */
            wifiManager.setWifiEnabled(true);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        adapterPub = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListPub);
        listView.setAdapter(adapter);
        listViewPub.setAdapter(adapterPub);
        listViewPub.setBackgroundColor(Color.GRAY);
    }

    /***
     *  scanWifi() - calls startScan() - notes
     *  startScan() to be deprecated in newer Android releases - need work a round
     *  startScam() returns true on success - multiple reasons for false response
     *  on false - may still be able to catch caches scan info - better than nada
     */
    private void scanWifi() {
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.EXTRA_RESULTS_UPDATED));
        if (wifiManager.startScan())
            Toast.makeText(this, "Trying Wifi Scan...", Toast.LENGTH_SHORT).show();
        results = wifiManager.getScanResults();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        /**
         * onReceive() is called when IntentFilter fires for filtered events
         * in this case that we have scan data to use - overrides onReceive to
         * call follow-on logic
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(
                    WifiManager.EXTRA_RESULTS_UPDATED, true);
            Toast.makeText(context, "scanSuccess()", Toast.LENGTH_SHORT).show();
            scanSuccess(context, intent);
        }

        /**
         * scanSuccess() - this is where the bulk of the work will be done on the scan data
         * @param context
         * @param intent
         */

        public void scanSuccess(Context context, Intent intent) {
            Toast.makeText(context, "scanSuccess()", Toast.LENGTH_SHORT).show();
            results = wifiManager.getScanResults();
            unregisterReceiver(this);

            arrayList.clear();
            arrayListPub.clear();
            adapter.notifyDataSetChanged();
            adapterPub.notifyDataSetChanged();
            // after scan lookup results with web service
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                    .build();
            GetOIUDataService ouiDataService = retrofit.create(GetOIUDataService.class);

            for (ScanResult scanResult : results) {
                final AccessPoint ap = new AccessPoint(scanResult.BSSID, scanResult.SSID, scanResult.capabilities);
                String ouiString = scanResult.BSSID.substring(0,8);
                OUIqry o = new OUIqry(ouiString);
                Call<OUI> call = ouiDataService.lookup_oui(o);
                Toast.makeText(MainActivity.this, "looping result" + call.toString(), Toast.LENGTH_SHORT);
                call.enqueue(new Callback<OUI>() {
                    /*** call back must override onResponse and onFailure
                     *
                     */
                    @Override
                    public void onResponse(Call<OUI> call, Response<OUI> response) {
                        OUI thisResponse = response.body();
                        if (thisResponse == null) /* had weird responses where response.body() returns null - kludge for now */
                            thisResponse = new OUI("xxx","xxx"+ response.toString(), "xxx" + response.raw().toString());
                        if (ap.isEncrypted()) { /* encryption is check point for "public" vs private"  - encrypted mostly = private */
                            arrayList.add("SSID:" + ap.getSSID() + " - BSSID:" + ap.getBSSID() + " - Encrypted:" + ap.isEncrypted() +
                                    " - OUI Long name - " + thisResponse.getLongname() + " - OUI Short name - " + thisResponse.getShortname());
                            adapter.notifyDataSetChanged();
                        } else { /* assume either public or bad */
                            arrayListPub.add("SSID:" + ap.getSSID() + " - BSSID:" + ap.getBSSID() + " - Encrypted:" + ap.isEncrypted() +
                                    " - OUI Long name - " + thisResponse.getLongname() + " - OUI Short name - " + thisResponse.getShortname());
                            adapterPub.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<OUI> call, Throwable t) {
                        /* if the webservice call fails - fall back to known info */
                        Toast.makeText(MainActivity.this, "Something went wrong...Please try later!" + t.toString(), Toast.LENGTH_LONG).show();
                        if (ap.isEncrypted()) {
                            arrayList.add("SSID:" + ap.getSSID() + " - BSSID:" + ap.getBSSID() + " - Encrypted:" + ap.isEncrypted());
                            adapter.notifyDataSetChanged();
                        } else {
                            arrayListPub.add("SSID:" + ap.getSSID() + " - BSSID:" + ap.getBSSID() + " - Encrypted:" + ap.isEncrypted());
                            adapterPub.notifyDataSetChanged();
                        }
                    }
                });

            }
        }

    };
}