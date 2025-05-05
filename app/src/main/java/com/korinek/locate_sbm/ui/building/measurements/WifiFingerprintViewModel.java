package com.korinek.locate_sbm.ui.building.measurements;

import android.app.Application;
import android.net.wifi.ScanResult;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.korinek.locate_sbm.R;
import com.korinek.locate_sbm.database.AppDatabase;
import com.korinek.locate_sbm.mapper.WifiFingerprintMapper;
import com.korinek.locate_sbm.model.WifiFingerprint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WifiFingerprintViewModel extends AndroidViewModel {

    private final AppDatabase database;
    WifiScanService wifiScanService;
    private final MediatorLiveData<List<ScanResult>> wifiScanResults = new MediatorLiveData<>();
    private final int NUMBER_OF_WIFI_FINGERPRINTS = 10;
    private final List<WifiFingerprint> wifiFingerprintList = new ArrayList<>();
    private final int roomId;

    public WifiFingerprintViewModel(Application application, int roomId) {
        super(application);
        this.roomId = roomId;
        database = AppDatabase.getInstance(application.getApplicationContext());
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
        WifiFingerprint wifiFingerprint = WifiFingerprintMapper.scanResultsToWifiFingerprint(wifiList, roomId);
        wifiFingerprintList.add(wifiFingerprint);
    }

    public void saveAllFingerprints() {
        // save all fingerprints to database - save copy of the list because of ConcurrentModificationException
        List<WifiFingerprint> wifiFingerprintsToSave = new ArrayList<>(wifiFingerprintList);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> database.roomDao().saveWifiFingerprintsList(roomId, wifiFingerprintsToSave));
        Toast.makeText(getApplication().getApplicationContext(), getApplication().getString(R.string.toast_room_calibrated), Toast.LENGTH_SHORT).show();

        wifiFingerprintList.clear();
    }

    public void reset() {
        wifiFingerprintList.clear();
    }

    public static class WifiFingerprintViewModelFactory implements ViewModelProvider.Factory {
        private final Application application;
        private final int roomId;

        public WifiFingerprintViewModelFactory(Application application, int roomId) {
            this.application = application;
            this.roomId = roomId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(WifiFingerprintViewModel.class)) {
                return (T) new WifiFingerprintViewModel(application, roomId);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
