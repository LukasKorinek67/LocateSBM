package com.korinek.locate_sbm.ui.localization.accelerometer;

import android.app.Application;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AccelerometerViewModel extends AndroidViewModel implements SensorEventListener {

    private final SensorManager sensorManager;
    private final Sensor accelerometerSensor;
    private final MutableLiveData<float[]> accelerometerData = new MutableLiveData<>();
    private boolean isAccelerometerActive = false;

    public AccelerometerViewModel(@NonNull Application application) {
        super(application);
        sensorManager = (SensorManager) application.getSystemService(Application.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public LiveData<float[]> getAccelerometerData() {
        return accelerometerData;
    }

    public boolean isAccelerometerActive() {
        return isAccelerometerActive;
    }

    public void startListening() {
        if (accelerometerSensor != null && !isAccelerometerActive) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            isAccelerometerActive = true;
        }
    }

    public void stopListening() {
        if (isAccelerometerActive) {
            sensorManager.unregisterListener(this);
            isAccelerometerActive = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerData.postValue(new float[]{event.values[0], event.values[1], event.values[2]});
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No need right now
    }
}
