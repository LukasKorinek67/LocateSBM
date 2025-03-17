package com.korinek.locate_sbm.ui.building.measurements;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RoomScanningStateViewModel extends ViewModel {

    public enum ScanState {
        NOT_STARTED,
        LOADING,
        DONE,
        FAILED
    }

    private final MutableLiveData<ScanState> scanState = new MutableLiveData<>(ScanState.NOT_STARTED);
    private final MutableLiveData<Integer> numberOfScans = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> scanCount = new MutableLiveData<>(1);

    public LiveData<ScanState> getScanState() {
        return scanState;
    }

    public void setScanState(ScanState newState) {
        scanState.setValue(newState);
    }

    public LiveData<Integer> getScanCount() {
        return scanCount;
    }

    public boolean isLastScan() {
        return scanCount.getValue().equals(numberOfScans.getValue());
    }

    public void goToNextScan() {
        if(scanCount.getValue() < numberOfScans.getValue()) {
            scanCount.setValue(scanCount.getValue() + 1);
        }
    }

    public MutableLiveData<Integer> getNumberOfScans() {
        return numberOfScans;
    }

    public void setNumberOfScans(int numberOfScans) {
        this.numberOfScans.setValue(numberOfScans);
    }

    public void reset() {
        scanState.setValue(ScanState.NOT_STARTED);
        scanCount.setValue(1);
    }
}
