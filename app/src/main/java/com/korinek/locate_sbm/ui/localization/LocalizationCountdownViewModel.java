package com.korinek.locate_sbm.ui.localization;

import android.os.CountDownTimer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LocalizationCountdownViewModel extends ViewModel {

    private static final long LOCALIZATION_INTERVAL = 10000; // 10 sec
    private final MutableLiveData<Integer> countdownTime = new MutableLiveData<>();
    private final MutableLiveData<Boolean> localizationTrigger = new MutableLiveData<>();
    private CountDownTimer countDownTimer;

    public LocalizationCountdownViewModel() {
        startCountdown();
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
                performLocalization();
                start();
            }
        }.start();
    }

    public void performLocalization() {
        localizationTrigger.postValue(true);
    }

    public void refreshLocalizationNow() {
        performLocalization();
        startCountdown();
    }

    public LiveData<Integer> getCountdownTime() {
        return countdownTime;
    }

    public LiveData<Boolean> getLocalizationTrigger() {
        return localizationTrigger;
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
