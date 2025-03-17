package com.korinek.locate_sbm.ui.building.measurements;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.funrisestudio.stepprogress.StepProgressView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.korinek.locate_sbm.R;
import com.korinek.locate_sbm.databinding.FragmentRoomScanningBinding;
import com.korinek.locate_sbm.ui.localization.WifiListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RoomScanningFragment extends Fragment {
    private FragmentRoomScanningBinding binding;
    private RoomScanningViewModel roomScanningViewModel;
    private WifiFingerprintViewModel wifiFingerprintViewModel;
    private WifiListAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        roomScanningViewModel = new ViewModelProvider(requireActivity()).get(RoomScanningViewModel.class);
        wifiFingerprintViewModel = new ViewModelProvider(this).get(WifiFingerprintViewModel.class);
        binding = FragmentRoomScanningBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initializeComponents();
        return root;
    }

    private void initializeComponents() {
        ViewFlipper viewFlipper = binding.measurementsViewFlipper;
        Button getFingerprintButton = binding.getFingerprintButton;
        Button repeatScanButton = binding.repeatScanButton;
        Button saveFingerprintButton = binding.saveFingerprintButton;
        Button errorRepeatScanButton = binding.errorRepeatScanButton;
        RecyclerView wifiFingerprintList = binding.wifiFingerprintsList;
        StepProgressView stepProgressView = binding.stepProgress;

        // buttons onClickListeners
        getFingerprintButton.setOnClickListener(v -> {
            roomScanningViewModel.setScanState(RoomScanningViewModel.ScanState.LOADING);
            wifiFingerprintViewModel.startWifiScan();
        });
        repeatScanButton.setOnClickListener(v -> {
            roomScanningViewModel.setScanState(RoomScanningViewModel.ScanState.LOADING);
            wifiFingerprintViewModel.startWifiScan();
        });
        errorRepeatScanButton.setOnClickListener(v -> {
            roomScanningViewModel.setScanState(RoomScanningViewModel.ScanState.LOADING);
            wifiFingerprintViewModel.startWifiScan();
        });
        saveFingerprintButton.setOnClickListener(v -> {
            wifiFingerprintViewModel.addFingerprintToCalibration();
            goToNextScan();
        });

        // Wi-Fi list
        List<ScanResult> wifiList = new ArrayList<>();
        adapter = new WifiListAdapter(wifiList);
        wifiFingerprintList.setAdapter(adapter);
        wifiFingerprintList.setLayoutManager(new LinearLayoutManager(requireContext()));

        wifiFingerprintViewModel.getWifiScanResults().observe(getViewLifecycleOwner(), this::updateWifiScanResults);

        // state observing - show corresponding view
        roomScanningViewModel.getScanState().observe(getViewLifecycleOwner(), state -> {
            switch (state) {
                case NOT_STARTED:
                    viewFlipper.setDisplayedChild(0);
                    break;
                case LOADING:
                    viewFlipper.setDisplayedChild(1);
                    break;
                case DONE:
                    viewFlipper.setDisplayedChild(2);
                    break;
                case FAILED:
                    viewFlipper.setDisplayedChild(3);
                    break;
            }
        });

        // update room scanning title - number in text
        roomScanningViewModel.getScanCount().observe(getViewLifecycleOwner(), measurementCount -> {
            TextView roomScanningTitle = binding.roomScanningTitle;
            roomScanningTitle.setText(String.format(Locale.getDefault(), getString(R.string.room_scanning_title), measurementCount));
        });

        // set number of steps on StepProgressView
        roomScanningViewModel.getNumberOfScans().observe(getViewLifecycleOwner(), stepProgressView::setStepsCount);
    }

    private void updateWifiScanResults(List<ScanResult> wifiList) {
        if (wifiList == null || wifiList.isEmpty()) {
            roomScanningViewModel.setScanState(RoomScanningViewModel.ScanState.FAILED);
        } else {
            roomScanningViewModel.setScanState(RoomScanningViewModel.ScanState.DONE);
        }
        adapter.updateWifiList(wifiList);
    }

    private void goToNextScan() {
        if(roomScanningViewModel.isLastScan()) {
            // save fingerprints and dismiss bottomSheetDialog
            wifiFingerprintViewModel.saveAllFingerprints();
            BottomSheetDialogFragment parent = (BottomSheetDialogFragment) getParentFragment();
            if(parent != null) {
                parent.dismiss();
            }
        } else {
            // next scan and update stepProgressView
            roomScanningViewModel.goToNextScan();
            roomScanningViewModel.setScanState(RoomScanningViewModel.ScanState.NOT_STARTED);
            StepProgressView stepProgressView = binding.stepProgress;
            stepProgressView.nextStep(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        roomScanningViewModel.reset();
    }
}
