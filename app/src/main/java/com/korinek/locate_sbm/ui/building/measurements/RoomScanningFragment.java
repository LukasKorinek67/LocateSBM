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
import com.korinek.locate_sbm.ui.building.BuildingViewModel;
import com.korinek.locate_sbm.ui.localization.WifiListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RoomScanningFragment extends Fragment {
    private FragmentRoomScanningBinding binding;
    private RoomScanningStateViewModel roomScanningStateViewModel;
    private WifiFingerprintViewModel wifiFingerprintViewModel;
    BuildingViewModel buildingViewModel;
    private WifiListAdapter adapter;
    private static final String ARG_ROOM_ID = "roomId";
    private int roomId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            roomId = getArguments().getInt(ARG_ROOM_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        roomScanningStateViewModel = new ViewModelProvider(requireActivity()).get(RoomScanningStateViewModel.class);
        WifiFingerprintViewModel.WifiFingerprintViewModelFactory factory = new WifiFingerprintViewModel.WifiFingerprintViewModelFactory(requireActivity().getApplication(), roomId);
        wifiFingerprintViewModel = new ViewModelProvider(this, factory).get(WifiFingerprintViewModel.class);
        buildingViewModel = new ViewModelProvider(requireActivity()).get(BuildingViewModel.class);
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
            roomScanningStateViewModel.setScanState(RoomScanningStateViewModel.ScanState.LOADING);
            wifiFingerprintViewModel.startWifiScan();
        });
        repeatScanButton.setOnClickListener(v -> {
            roomScanningStateViewModel.setScanState(RoomScanningStateViewModel.ScanState.LOADING);
            wifiFingerprintViewModel.startWifiScan();
        });
        errorRepeatScanButton.setOnClickListener(v -> {
            roomScanningStateViewModel.setScanState(RoomScanningStateViewModel.ScanState.LOADING);
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
        roomScanningStateViewModel.getScanState().observe(getViewLifecycleOwner(), state -> {
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
        roomScanningStateViewModel.getScanCount().observe(getViewLifecycleOwner(), measurementCount -> {
            TextView roomScanningTitle = binding.roomScanningTitle;
            roomScanningTitle.setText(String.format(Locale.getDefault(), getString(R.string.room_scanning_title), measurementCount));
        });

        // set number of steps on StepProgressView
        roomScanningStateViewModel.getNumberOfScans().observe(getViewLifecycleOwner(), stepProgressView::setStepsCount);
    }

    private void updateWifiScanResults(List<ScanResult> wifiList) {
        if(roomScanningStateViewModel.getScanState().getValue().equals(RoomScanningStateViewModel.ScanState.LOADING)) {
            if (wifiList == null || wifiList.isEmpty()) {
                roomScanningStateViewModel.setScanState(RoomScanningStateViewModel.ScanState.FAILED);
            } else {
                roomScanningStateViewModel.setScanState(RoomScanningStateViewModel.ScanState.DONE);
            }
            adapter.updateWifiList(wifiList);
        }
    }

    private void goToNextScan() {
        if(roomScanningStateViewModel.isLastScan()) {
            // save fingerprints and dismiss bottomSheetDialog
            wifiFingerprintViewModel.saveAllFingerprints();
            buildingViewModel.reloadRooms();
            BottomSheetDialogFragment parent = (BottomSheetDialogFragment) getParentFragment();
            if(parent != null) {
                parent.dismiss();
            }
        } else {
            // next scan and update stepProgressView
            roomScanningStateViewModel.goToNextScan();
            roomScanningStateViewModel.setScanState(RoomScanningStateViewModel.ScanState.NOT_STARTED);
            StepProgressView stepProgressView = binding.stepProgress;
            stepProgressView.nextStep(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        roomScanningStateViewModel.reset();
        wifiFingerprintViewModel.reset();
    }
}
