package com.korinek.locate_sbm.ui.localization.gyroscope;

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

import com.korinek.locate_sbm.databinding.FragmentGyroscopeBinding;

public class GyroscopeFragment extends Fragment {

    private FragmentGyroscopeBinding binding;
    private GyroscopeViewModel gyroscopeViewModel;
    private TextView gyroscopeValues;
    private SwitchCompat switchGyroscope;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        gyroscopeViewModel = new ViewModelProvider(this).get(GyroscopeViewModel.class);
        binding = FragmentGyroscopeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        gyroscopeValues = binding.gyroscopeValues;
        switchGyroscope = binding.switchGyroscope;

        switchGyroscope.setChecked(gyroscopeViewModel.isGyroscopeActive());
        resetGyroscopeData();

        addListenerAndObserver();

        return root;
    }

    private void addListenerAndObserver() {
        switchGyroscope.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                gyroscopeViewModel.startListening();
            } else {
                gyroscopeViewModel.stopListening();
                resetGyroscopeData();
            }
        });

        gyroscopeViewModel.getGyroscopeData().observe(getViewLifecycleOwner(), values -> {
            if (values != null) {
                gyroscopeValues.setText(String.format("x: %5.2f\ny: %5.2f\nz: %5.2f", values[0], values[1], values[2]));
            }
        });
    }

    private void resetGyroscopeData() {
        gyroscopeValues.setText(String.format("x: %5.2f\ny: %5.2f\nz: %5.2f", 0.0, 0.0, 0.0));
    }
}
