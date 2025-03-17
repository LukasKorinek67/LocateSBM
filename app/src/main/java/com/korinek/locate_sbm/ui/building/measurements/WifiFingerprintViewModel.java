package com.korinek.locate_sbm.ui.building.measurements;

import android.app.Application;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

public class WifiFingerprintViewModel extends AndroidViewModel {

    WifiScanService wifiScanService;
    private final MediatorLiveData<List<ScanResult>> wifiScanResults = new MediatorLiveData<>();
    private final int NUMBER_OF_WIFI_FINGERPRINTS = 5;

    public WifiFingerprintViewModel(Application application) {
        super(application);
        wifiScanService = WifiScanService.getInstance(application);
        wifiScanResults.addSource(wifiScanService.getWifiScanResults(), scanResults -> {
            if (scanResults.size() > NUMBER_OF_WIFI_FINGERPRINTS) {
                scanResults = scanResults.subList(0, NUMBER_OF_WIFI_FINGERPRINTS);
            }
            wifiScanResults.setValue(scanResults);
        });
    }
    public LiveData<List<ScanResult>> getWifiScanResults() {
        return wifiScanResults;
    }

    public void startWifiScan() {
        wifiScanService.startWifiScan();
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
}
