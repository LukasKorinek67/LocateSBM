package com.korinek.locate_sbm.ui.localization;

import android.app.Application;
import android.net.wifi.ScanResult;
import android.os.CountDownTimer;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.korinek.locate_sbm.model.Room;
import com.korinek.locate_sbm.ui.building.measurements.WifiScanService;

import java.util.List;

public class LocalizationCountdownViewModel extends AndroidViewModel {

    WifiScanService wifiScanService;
    private final MediatorLiveData<List<ScanResult>> wifiScanResults = new MediatorLiveData<>();
    private final MutableLiveData<List<Room>> locationSortedRooms = new MutableLiveData<>();
    private static final long LOCALIZATION_INTERVAL = 10000; // 10 sec
    private final MutableLiveData<Integer> countdownTime = new MutableLiveData<>();
    private CountDownTimer countDownTimer;

    public LocalizationCountdownViewModel(Application application) {
        super(application);
        wifiScanService = WifiScanService.getInstance(application);
        wifiScanResults.addSource(wifiScanService.getWifiScanResults(), scanResults -> {
            // after wifi scan, results in live data getWifiScanResults()

            // set wifi scan results to show in UI
            wifiScanResults.setValue(scanResults);

            // sort rooms by location
            performLocalization(scanResults);
        });
        performWifiScan();
    }

    private void performWifiScan() {
        wifiScanService.startWifiScan();
    }

    private void performLocalization(List<ScanResult> scanResults) {
        // TODO - map scanResult to WifiFingerprint and pass it to sortRoomsByLocation()
        List<Room> sortedRooms = RoomLocationSorter.sortRoomsByLocation(locationSortedRooms.getValue());
        locationSortedRooms.setValue(sortedRooms);

        // start new countdown
        startCountdown();
    }

    public LiveData<List<ScanResult>> getWifiScanResults() {
        return wifiScanResults;
    }

    public MutableLiveData<List<Room>> getLocationSortedRooms() {
        return locationSortedRooms;
    }

    public void setLocationSortedRooms(List<Room> rooms) {
        locationSortedRooms.setValue(rooms);
    }

    private void startCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(LOCALIZATION_INTERVAL, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countdownTime.postValue((int) ((millisUntilFinished / 1000) + 1));
            }

            @Override
            public void onFinish() {
                countdownTime.postValue(0);
                performWifiScan();
            }
        }.start();
    }

    public void refreshLocalizationNow() {
        performWifiScan();
    }

    public LiveData<Integer> getCountdownTime() {
        return countdownTime;
    }

    public void restartCountdown() {
        startCountdown();
    }

    public void stopCountdownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
