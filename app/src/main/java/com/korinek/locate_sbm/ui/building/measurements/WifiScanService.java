package com.korinek.locate_sbm.ui.building.measurements;

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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.korinek.locate_sbm.R;

import java.util.List;

public class WifiScanService {

    private static WifiScanService instance;
    private final WifiManager wifiManager;
    private final BroadcastReceiver wifiReceiver;
    private final MutableLiveData<List<ScanResult>> wifiScanResults = new MutableLiveData<>();
    private final Context appContext;
    private boolean isReceiverRegistered;

    private WifiScanService(Context context) {
        this.appContext = context.getApplicationContext();
        wifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null && intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    List<ScanResult> results;
                    if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        results = wifiManager.getScanResults();
                        if (results.isEmpty()) {
                            Log.d("WifiAnalysisViewModel", "No WiFi networks found.");
                        } else {
                            sortResults(results);
                        }
                        wifiScanResults.postValue(results);
                    } else {
                        Toast.makeText(appContext, appContext.getString(R.string.toast_localization_permission_not_granted), Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        appContext.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        isReceiverRegistered = true;
    }

    public static synchronized WifiScanService getInstance(Context context) {
        if (instance == null) {
            instance = new WifiScanService(context);
        }
        return instance;
    }

    private void sortResults(List<ScanResult> results) {
        results.sort((result1, result2) -> Integer.compare(result2.level, result1.level));
    }

    public void startWifiScan() {
        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            wifiManager.startScan();
        } else {
            Toast.makeText(appContext, appContext.getString(R.string.toast_localization_permission_not_granted), Toast.LENGTH_LONG).show();
        }
    }

    public LiveData<List<ScanResult>> getWifiScanResults() {
        return wifiScanResults;
    }

    public void unregisterReceiver() {
        if (isReceiverRegistered) {
            appContext.unregisterReceiver(wifiReceiver);
            isReceiverRegistered = false;
        }
    }
}
