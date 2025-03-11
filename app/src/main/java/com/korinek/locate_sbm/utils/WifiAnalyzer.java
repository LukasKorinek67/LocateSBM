package com.korinek.locate_sbm.utils;

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

import com.korinek.locate_sbm.R;

import java.util.ArrayList;
import java.util.List;

public class WifiAnalyzer {
    private static final String TAG = "WifiAnalyzer";
    private final WifiManager wifiManager;
    private final Context context;

    public WifiAnalyzer(Context context) {
        this.context = context;
        this.wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public interface WifiScanCallback {
        void onWifiScanSuccess(List<ScanResult> results);
        void onWifiScanFailure(String errorMessage);
    }

    public void scanAndGetWifi(WifiScanCallback callback) {
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
                    List<ScanResult> results = getScanResult();
                    callback.onWifiScanSuccess(results);
                } else {
                    callback.onWifiScanFailure(context.getString(R.string.toast_scan_failed));
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
            Toast.makeText(this.context, this.context.getString(R.string.toast_scan_failed), Toast.LENGTH_LONG).show();
        }
    }

    private List<ScanResult> getScanResult() {
        List<ScanResult> results = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            results = this.wifiManager.getScanResults();
            if (results.isEmpty()) {
                Log.d(TAG, "No WiFi networks found.");
            } else {
                sortResults(results);
            }
        } else {
            Toast.makeText(this.context, this.context.getString(R.string.toast_localization_permission_not_granted), Toast.LENGTH_LONG).show();
        }
        return results;
    }

    private void sortResults(List<ScanResult> results) {
        results.sort((result1, result2) -> Integer.compare(result2.level, result1.level));
    }
}
