package com.korinek.locate_sbm.ui.localization.wifi_analysis;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.korinek.locate_sbm.R;
import com.korinek.locate_sbm.databinding.FragmentWifiAnalysisBinding;
import com.korinek.locate_sbm.ui.localization.WifiListAdapter;

import java.util.ArrayList;
import java.util.List;

public class WifiAnalysisFragment extends Fragment {

    private FragmentWifiAnalysisBinding binding;
    private WifiAnalysisViewModel wifiAnalysisViewModel;
    private SwitchCompat switchWifiAnalysis;
    private ProgressBar progressBar;
    private Button moreResultsButton;
    private TextView wifiAnalysisInfoText;

    private WifiListAdapter adapter;

    private final int NUMBER_OF_SHOWN_WIFI = 4;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        wifiAnalysisViewModel = new ViewModelProvider(this).get(WifiAnalysisViewModel.class);
        binding = FragmentWifiAnalysisBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        switchWifiAnalysis = binding.switchWifiAnalysis;
        progressBar = binding.progressBar;
        moreResultsButton = binding.moreResultsButton;
        wifiAnalysisInfoText = binding.wifiAnalysisInfoText;

        initialize();

        return root;
    }

    private void initialize() {
        switchWifiAnalysis.setChecked(wifiAnalysisViewModel.isWifiAnalysisActive());
        progressBar.setVisibility(View.GONE);
        moreResultsButton.setVisibility(View.GONE);
        binding.wifiAnalysisInfoText.setVisibility(View.VISIBLE);

        switchWifiAnalysis.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                wifiAnalysisViewModel.startWifiScanning();
                progressBar.setVisibility(View.VISIBLE);
                wifiAnalysisInfoText.setVisibility(View.GONE);
            } else {
                wifiAnalysisViewModel.stopWifiScanning();
                resetWifiData();
            }
        });

        List<ScanResult> wifiList = new ArrayList<>();
        RecyclerView recyclerView = binding.recyclerViewWifiList;
        adapter = new WifiListAdapter(wifiList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        wifiAnalysisViewModel.getWifiScanResults().observe(getViewLifecycleOwner(), this::updateWifiScanResults);
        initializeMoreButton();
    }

    private void resetWifiData() {
        List<ScanResult> wifiList = new ArrayList<>();
        adapter.updateWifiList(wifiList);
        progressBar.setVisibility(View.GONE);
        moreResultsButton.setVisibility(View.GONE);
        wifiAnalysisInfoText.setVisibility(View.VISIBLE);
        wifiAnalysisInfoText.setText(getString(R.string.wifi_analysis_info_scan_off));
    }

    private void updateWifiScanResults(List<ScanResult> wifiList) {
        if(wifiAnalysisViewModel.isWifiAnalysisActive()) {
            progressBar.setVisibility(View.GONE);

            if (wifiList == null || wifiList.isEmpty()) {
                moreResultsButton.setVisibility(View.GONE);
                wifiAnalysisInfoText.setVisibility(View.VISIBLE);
                wifiAnalysisInfoText.setText(getString(R.string.wifi_analysis_info_no_wifi));
            } else if (wifiList.size() <= NUMBER_OF_SHOWN_WIFI) {
                moreResultsButton.setVisibility(View.GONE);
                wifiAnalysisInfoText.setVisibility(View.GONE);
            } else {
                wifiList = wifiList.subList(0, NUMBER_OF_SHOWN_WIFI);
                moreResultsButton.setVisibility(View.VISIBLE);
                wifiAnalysisInfoText.setVisibility(View.GONE);
            }
            adapter.updateWifiList(wifiList);
        }
    }

    private void initializeMoreButton() {
        moreResultsButton.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(requireContext());
            View dialogView = inflater.inflate(R.layout.dialog_wifi_list, null);

            if(wifiAnalysisViewModel.getWifiScanResults().getValue() != null) {
                List<ScanResult> wifiList = wifiAnalysisViewModel.getWifiScanResults().getValue();

                RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view_wifi_list);
                WifiListAdapter adapter = new WifiListAdapter(wifiList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

                new AlertDialog.Builder(requireContext())
                        .setView(dialogView)
                        .setPositiveButton(getString(R.string.close), (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
            }
        });
    }
}
