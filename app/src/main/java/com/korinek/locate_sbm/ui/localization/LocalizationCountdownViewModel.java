package com.korinek.locate_sbm.ui.localization;

import android.app.Application;
import android.net.wifi.ScanResult;
import android.os.CountDownTimer;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.korinek.locate_sbm.mapper.RoomMapper;
import com.korinek.locate_sbm.mapper.WifiFingerprintMapper;
import com.korinek.locate_sbm.model.LocalizedRoom;
import com.korinek.locate_sbm.model.RoomWithWifiFingerprints;
import com.korinek.locate_sbm.model.WifiFingerprint;
import com.korinek.locate_sbm.ui.building.measurements.WifiScanService;

import java.util.ArrayList;
import java.util.List;

public class LocalizationCountdownViewModel extends AndroidViewModel {

    WifiScanService wifiScanService;
    private final MediatorLiveData<List<ScanResult>> wifiScanResults = new MediatorLiveData<>();
    private final MutableLiveData<List<LocalizedRoom>> locationSortedRooms = new MutableLiveData<>(new ArrayList<>());
    private static final long LOCALIZATION_INTERVAL = 10000; // 10 sec
    private final MutableLiveData<Integer> countdownTime = new MutableLiveData<>();
    private CountDownTimer countDownTimer;
    private final MutableLiveData<Boolean> localizationDone = new MutableLiveData<>(false);
    private long remainingTimeMillis = LOCALIZATION_INTERVAL;
    private boolean isCountdownPaused = false;

    public LocalizationCountdownViewModel(Application application) {
        super(application);
        wifiScanService = WifiScanService.getInstance(application);
        wifiScanResults.addSource(wifiScanService.getWifiScanResults(), scanResults -> {
            // after wifi scan, results in live data getWifiScanResults()

            // set wifi scan results to show in UI
            wifiScanResults.setValue(scanResults);

            performLocalization(scanResults);
        });
        performWifiScan();
    }

    private void performWifiScan() {
        localizationDone.setValue(false);
        wifiScanService.startWifiScan();
    }

    private void performLocalization(List<ScanResult> scanResults) {
        if(locationSortedRooms.getValue() != null || !locationSortedRooms.getValue().isEmpty()) {
            WifiFingerprint actualFingerprint = WifiFingerprintMapper.scanResultsToWifiFingerprint(scanResults);
            List<LocalizedRoom> sortedRooms = RoomLocationSorter.sortRoomsByLocation(actualFingerprint, locationSortedRooms.getValue(), getApplication());
            locationSortedRooms.setValue(sortedRooms);
        }
        localizationDone.setValue(true);

        // start new countdown
        startCountdown();
    }

    public LiveData<List<ScanResult>> getWifiScanResults() {
        return wifiScanResults;
    }

    public MutableLiveData<List<LocalizedRoom>> getLocationSortedRooms() {
        return locationSortedRooms;
    }

    public void setRooms(List<RoomWithWifiFingerprints> rooms) {
        locationSortedRooms.setValue(RoomMapper.toLocalizedRoomList(rooms));
    }

    public MutableLiveData<Boolean> isLocalizationDone() {
        return localizationDone;
    }

    private void startCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(remainingTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTimeMillis = millisUntilFinished;
                countdownTime.postValue((int) ((millisUntilFinished / 1000) + 1));
            }

            @Override
            public void onFinish() {
                countdownTime.postValue(0);
                remainingTimeMillis = LOCALIZATION_INTERVAL;
                performWifiScan();
            }
        }.start();
        isCountdownPaused = false;
    }

    public void refreshLocalizationNow() {
        performWifiScan();
        remainingTimeMillis = LOCALIZATION_INTERVAL;
    }

    public LiveData<Integer> getCountdownTime() {
        return countdownTime;
    }

    public void restartCountdown() {
        performWifiScan();
    }

    public void stopCountdownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void pauseCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            isCountdownPaused = true;
        }
    }

    public void resumeCountdown() {
        if (isCountdownPaused) {
            startCountdown();
        }
    }

    public boolean isCountdownPaused() {
        return isCountdownPaused;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
