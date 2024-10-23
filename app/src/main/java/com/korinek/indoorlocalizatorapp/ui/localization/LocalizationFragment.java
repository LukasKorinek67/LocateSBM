package com.korinek.indoorlocalizatorapp.ui.localization;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.korinek.indoorlocalizatorapp.databinding.FragmentLocalizationBinding;

public class LocalizationFragment extends Fragment {

    private FragmentLocalizationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LocalizationViewModel localizationViewModel =
                new ViewModelProvider(this).get(LocalizationViewModel.class);

        binding = FragmentLocalizationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button wifiButton = binding.buttonWifiAnalysis;
        wifiButton.setOnClickListener(v -> Log.d("LocalizationFragment", "Wifi Analýza button clicked"));

        final TextView textView = binding.textLocalization;
        localizationViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}