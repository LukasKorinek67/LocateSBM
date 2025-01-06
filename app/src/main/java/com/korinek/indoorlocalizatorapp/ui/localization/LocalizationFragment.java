package com.korinek.indoorlocalizatorapp.ui.localization;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.korinek.indoorlocalizatorapp.R;
import com.korinek.indoorlocalizatorapp.databinding.FragmentLocalizationBinding;
import com.korinek.indoorlocalizatorapp.ui.WifiListAdapter;
import com.korinek.indoorlocalizatorapp.utils.WifiAnalyzer;

import java.util.ArrayList;
import java.util.List;

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

        initializeWifiButton();

        //final TextView textView = binding.textLocalization;
        //localizationViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initializeWifiButton() {
        Button wifiButton = binding.buttonWifiAnalysis;
        wifiButton.setOnClickListener(v -> {
            Log.d("LocalizationFragment", "Wifi Analýza button clicked");

            binding.progressBar.setVisibility(View.VISIBLE);

            WifiAnalyzer wifiAnalyzer = new WifiAnalyzer(requireContext());
            wifiAnalyzer.scanAndGetWifi(new WifiAnalyzer.WifiScanCallback() {
                @Override
                public void onWifiScanSuccess(List<ScanResult> results) {
                    binding.progressBar.setVisibility(View.GONE);
                    showWifiDialog(results);
                }

                @Override
                public void onWifiScanFailure(String errorMessage) {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void showWifiDialog(List<ScanResult> results) {
        List<String> wifiList = new ArrayList<>();
        for (ScanResult result : results) {
            String ssid = (result.SSID == null || result.SSID.isBlank()) ? "[No-SSID]" : result.SSID;
            int signalStrength = result.level;
            Log.d("LocalizationFragment", "SSID: " + ssid + ", Signal Strength: " + signalStrength + " dBm");
            wifiList.add(ssid + ", " + signalStrength + " dBm");
        }

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_wifi_list, null);

        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view_wifi_list);
        WifiListAdapter adapter = new WifiListAdapter(wifiList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(requireContext()));

        // Vytvoření a zobrazení dialogu
        new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Zavřít", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}