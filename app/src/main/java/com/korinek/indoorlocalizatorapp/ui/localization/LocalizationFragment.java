package com.korinek.indoorlocalizatorapp.ui.localization;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.korinek.indoorlocalizatorapp.databinding.FragmentLocalizationBinding;
import com.korinek.indoorlocalizatorapp.utils.WifiAnalyzer;

public class LocalizationFragment extends Fragment {

    private FragmentLocalizationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LocalizationViewModel localizationViewModel =
                new ViewModelProvider(this).get(LocalizationViewModel.class);

        binding = FragmentLocalizationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // check and request for permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        Button wifiButton = binding.buttonWifiAnalysis;
        wifiButton.setOnClickListener(v -> {
            Log.d("LocalizationFragment", "Wifi Analýza button clicked");
            WifiAnalyzer wifiAnalyzer = new WifiAnalyzer(requireContext());
            wifiAnalyzer.scanWifi();
        });

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