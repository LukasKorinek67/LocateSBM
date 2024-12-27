package com.korinek.indoorlocalizatorapp.utils;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.korinek.indoorlocalizatorapp.R;

import java.util.List;

public class WifiAnalyzer {
    private static final String TAG = "WifiAnalyzer";
    private final WifiManager wifiManager;
    private final Context context;

    public WifiAnalyzer(Context context) {
        this.context = context;
        this.wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public void scanWifi() {
        // check if Wifi is enabled
        if (!this.wifiManager.isWifiEnabled()) {
            Log.e(TAG, "WiFi is disabled. Please enable it.");
            return;
        }

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                } else {
                    Log.e(TAG, "WiFi scan failed.");
                    scanFailure();
                }
                context.unregisterReceiver(this);
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        this.context.registerReceiver(wifiScanReceiver, intentFilter);

        // scaning
        boolean success = wifiManager.startScan();
        if (!success) {
            Log.e(TAG, "WiFi scan failed.");
            scanFailure();
        }
    }

    private void scanSuccess() {
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            List<ScanResult> results = this.wifiManager.getScanResults();
            if (results.isEmpty()) {
                Log.d(TAG, "No WiFi networks found.");
            } else {
                // sorting by SSID
                results.sort((result1, result2) -> result1.SSID.compareToIgnoreCase(result2.SSID));

                for (ScanResult result : results) {
                    String ssid = (result.SSID == null || result.SSID.isBlank()) ? "[No-SSID]" : result.SSID;
                    int signalStrength = result.level;
                    Log.d(TAG, "SSID: " + ssid + ", Signal Strength: " + signalStrength + " dBm");
                }
            }
        } else {
            Toast.makeText(this.context, this.context.getString(R.string.toast_localization_permission_not_granted), Toast.LENGTH_LONG).show();
        }
    }

    private void scanFailure() {
        /*
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            List<ScanResult> results = wifiManager.getScanResults();
            //... potentially use older scan results ...
        }
        */
        Toast.makeText(this.context, this.context.getString(R.string.toast_scan_failed), Toast.LENGTH_LONG).show();
    }
}
