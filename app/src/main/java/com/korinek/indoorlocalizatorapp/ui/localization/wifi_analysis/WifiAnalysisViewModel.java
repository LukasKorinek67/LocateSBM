package com.korinek.indoorlocalizatorapp.ui.localization.wifi_analysis;

import android.Manifest;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.korinek.indoorlocalizatorapp.R;

import java.util.ArrayList;
import java.util.List;

public class WifiAnalysisViewModel extends AndroidViewModel {

    private final WifiManager wifiManager;
    private final BroadcastReceiver wifiReceiver;
    private final Handler scanHandler = new Handler(Looper.getMainLooper());
    private boolean isWifiAnalysisActive = false;
    private final MutableLiveData<List<ScanResult>> wifiScanResults = new MutableLiveData<>();


    public WifiAnalysisViewModel(Application application) {
        super(application);
        wifiManager = (WifiManager) application.getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null && intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    List<ScanResult> results = new ArrayList<>();
                    if (ActivityCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        results = wifiManager.getScanResults();
                        if (results.isEmpty()) {
                            Log.d("WifiAnalysisViewModel", "No WiFi networks found.");
                        } else {
                            sortResults(results);
                        }
                        wifiScanResults.postValue(results);
                    } else {
                        Toast.makeText(application, application.getString(R.string.toast_localization_permission_not_granted), Toast.LENGTH_LONG).show();
                    }
                }
                //if (intent.getAction() != null && intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                //    wifiScanResults.postValue(wifiManager.getScanResults());
                //}
            }
        };

        /*BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
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
        };*/
        application.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    private void sortResults(List<ScanResult> results) {
        results.sort((result1, result2) -> Integer.compare(result2.level, result1.level));
    }

    public boolean isWifiAnalysisActive() {
        return isWifiAnalysisActive;
    }

    public void setWifiAnalysisActive(boolean wifiAnalysisActive) {
        isWifiAnalysisActive = wifiAnalysisActive;
    }

    public MutableLiveData<List<ScanResult>> getWifiScanResults() {
        return wifiScanResults;
    }

    public void startWifiScanning() {
        if (!isWifiAnalysisActive) {
            isWifiAnalysisActive = true;
            scanHandler.post(scanRunnable);
        }
    }

    public void stopWifiScanning() {
        if (isWifiAnalysisActive) {
            isWifiAnalysisActive = false;
            scanHandler.removeCallbacks(scanRunnable);
        }
    }

    private final Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            if (isWifiAnalysisActive) {
                wifiManager.startScan();
                scanHandler.postDelayed(this, 10000); // 10 seconds
            }
        }
    };

    @Override
    protected void onCleared() {
        super.onCleared();
        getApplication().unregisterReceiver(wifiReceiver);
        stopWifiScanning();
    }
}
