package com.korinek.indoorlocalizatorapp.ui.localization.accelerometer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.korinek.indoorlocalizatorapp.databinding.FragmentAccelerometerBinding;

public class AccelerometerFragment extends Fragment {

    private FragmentAccelerometerBinding binding;
    private AccelerometerViewModel accelerometerViewModel;
    private TextView accelerometerValues;
    private SwitchCompat switchAccelerometer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        accelerometerViewModel = new ViewModelProvider(this).get(AccelerometerViewModel.class);
        binding = FragmentAccelerometerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        accelerometerValues = binding.accelerometerValues;
        switchAccelerometer = binding.switchAccelerometer;

        switchAccelerometer.setChecked(accelerometerViewModel.isAccelerometerActive());
        resetAccelerometerData();

        addListenerAndObserver();

        return root;
    }

    private void addListenerAndObserver() {
        switchAccelerometer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                accelerometerViewModel.startListening();
            } else {
                accelerometerViewModel.stopListening();
                resetAccelerometerData();
            }
        });

        accelerometerViewModel.getAccelerometerData().observe(getViewLifecycleOwner(), values -> {
            if (values != null) {
                accelerometerValues.setText(String.format("x: %5.2f\ny: %5.2f\nz: %5.2f", values[0], values[1], values[2]));
            }
        });
    }

    private void resetAccelerometerData() {
        accelerometerValues.setText(String.format("x: %5.2f\ny: %5.2f\nz: %5.2f", 0.0, 0.0, 0.0));
    }
}
