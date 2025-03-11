package com.korinek.locate_sbm.ui.localization.gyroscope;


import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class GyroscopeViewModel extends AndroidViewModel implements SensorEventListener {

    private final SensorManager sensorManager;
    private final Sensor gyroscopeSensor;
    private final MutableLiveData<float[]> gyroscopeData = new MutableLiveData<>();
    private boolean isGyroscopeActive = false;

    public GyroscopeViewModel(@NonNull Application application) {
        super(application);
        sensorManager = (SensorManager) application.getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    public LiveData<float[]> getGyroscopeData() {
        return gyroscopeData;
    }

    public boolean isGyroscopeActive() {
        return isGyroscopeActive;
    }

    public void startListening() {
        if (gyroscopeSensor != null && !isGyroscopeActive) {
            sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_UI);
            isGyroscopeActive = true;
        }
    }

    public void stopListening() {
        if (isGyroscopeActive) {
            sensorManager.unregisterListener(this);
            isGyroscopeActive = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroscopeData.postValue(event.values);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No need right now
    }
}
