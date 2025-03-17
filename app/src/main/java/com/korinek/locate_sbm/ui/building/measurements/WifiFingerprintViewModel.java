package com.korinek.locate_sbm.ui.building.measurements;

import android.Manifest;
import android.app.Application;
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
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.korinek.locate_sbm.R;

import java.util.List;

public class WifiFingerprintViewModel extends AndroidViewModel {

    private final WifiManager wifiManager;
    private final BroadcastReceiver wifiReceiver;
    private final MutableLiveData<List<ScanResult>> wifiScanResults = new MutableLiveData<>();
    private final int NUMBER_OF_WIFI_FINGERPRINTS = 5;

    public WifiFingerprintViewModel(Application application) {
        super(application);
        wifiManager = (WifiManager) application.getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null && intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    List<ScanResult> results;
                    if (ActivityCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        results = wifiManager.getScanResults();
                        if (results.isEmpty()) {
                            Log.d("WifiAnalysisViewModel", "No WiFi networks found.");
                        } else {
                            sortResults(results);
                            if (results.size() > NUMBER_OF_WIFI_FINGERPRINTS) {
                                results = results.subList(0, NUMBER_OF_WIFI_FINGERPRINTS);
                            }
                        }
                        wifiScanResults.postValue(results);
                    } else {
                        Toast.makeText(application, application.getString(R.string.toast_localization_permission_not_granted), Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
        application.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    private void sortResults(List<ScanResult> results) {
        results.sort((result1, result2) -> Integer.compare(result2.level, result1.level));
    }

    public MutableLiveData<List<ScanResult>> getWifiScanResults() {
        return wifiScanResults;
    }

    public void startWifiScan() {
        if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            wifiManager.startScan();
        } else {
            Toast.makeText(getApplication(), getApplication().getString(R.string.toast_localization_permission_not_granted), Toast.LENGTH_LONG).show();
        }
    }

    public void addFingerprintToCalibration() {
        List<ScanResult> wifiList = wifiScanResults.getValue();
        // TODO - add to some list that will be later saved to database
        Log.d("MeasurementsBottomSheet", "Saving Wifi Fingerprints:");
        for (ScanResult scanResult : wifiList) {
            Log.d("MeasurementsBottomSheet", "SSID: " + scanResult.SSID + ", BSSID: " + scanResult.BSSID + ", RSSI: " + scanResult.level);
        }
    }

    public void saveAllFingerprints() {
        // TODO - save all fingerprints to database
        Toast.makeText(getApplication().getApplicationContext(), "Wifi fingerprints saved", Toast.LENGTH_SHORT).show();
    }

    public void reset() {
        // TODO - clear fingerprints list
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        getApplication().unregisterReceiver(wifiReceiver);
    }
}
