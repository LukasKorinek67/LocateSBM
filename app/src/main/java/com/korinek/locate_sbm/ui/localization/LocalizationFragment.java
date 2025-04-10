package com.korinek.locate_sbm.ui.localization;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.korinek.locate_sbm.R;
import com.korinek.locate_sbm.databinding.FragmentLocalizationBinding;
import com.korinek.locate_sbm.ui.building.BuildingViewModel;
import com.korinek.locate_sbm.ui.localization.localized_rooms.LocationSortedRoomAdapter;

import java.util.ArrayList;
import java.util.List;

public class LocalizationFragment extends Fragment {

    private FragmentLocalizationBinding binding;
    LocalizationCountdownViewModel localizationViewModel;
    BuildingViewModel buildingViewModel;
    private LocationSortedRoomAdapter locationSortedRoomAdapter;
    private WifiListAdapter wifiListAdapter;

    public enum LocalizationViewState {
        LOCALIZATION_IN_PROGRESS(0),
        LOCALIZED_ROOMS(1),
        NO_BUILDING_SELECTED(2),
        NO_ROOMS(3);

        private final int index;

        LocalizationViewState(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        localizationViewModel = new ViewModelProvider(requireActivity()).get(LocalizationCountdownViewModel.class);
        buildingViewModel = new ViewModelProvider(requireActivity()).get(BuildingViewModel.class);

        binding = FragmentLocalizationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // check and request for permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        //addFragmentsOnScreen();
        initialize();

        return root;
    }

    private void initialize() {
        ViewFlipper viewFlipper = binding.localizationViewFlipper;
        viewFlipper.setDisplayedChild(LocalizationViewState.LOCALIZATION_IN_PROGRESS.getIndex());

        initializeButtons();
        initializeCountdownTimer();
        initializeWifiListAdapter();
        initializeLocationSortedRoomAdapter();

        // get all rooms
        buildingViewModel.getRooms().observe(getViewLifecycleOwner(), rooms -> {
            localizationViewModel.setRooms(rooms);
            if(rooms.isEmpty() && buildingViewModel.getIsBuildingSelected().getValue()) {
                viewFlipper.setDisplayedChild(LocalizationViewState.NO_ROOMS.getIndex());
            } else {
                setViewFlipperChild(viewFlipper);
            }
        });

        // visibility of elements depends on selected building
        buildingViewModel.getIsBuildingSelected().observe(getViewLifecycleOwner(), isBuildingSelected -> {
            if (!isBuildingSelected) {
                viewFlipper.setDisplayedChild(LocalizationViewState.NO_BUILDING_SELECTED.getIndex());
            } else {
                setViewFlipperChild(viewFlipper);
            }
        });

        // visibility while localization in progress
        localizationViewModel.isLocalizationDone().observe(getViewLifecycleOwner(), isLocalizationDone -> {
            // if no building selected or empty room
            if(!buildingViewModel.getIsBuildingSelected().getValue() ||
                    (buildingViewModel.getRooms().getValue() != null && buildingViewModel.getRooms().getValue().isEmpty())) {
                setViewFlipperChild(viewFlipper);
            } else if (!isLocalizationDone) {
                viewFlipper.setDisplayedChild(LocalizationViewState.LOCALIZATION_IN_PROGRESS.getIndex());
            } else {
                viewFlipper.setDisplayedChild(LocalizationViewState.LOCALIZED_ROOMS.getIndex());
            }
        });

    }

    private void setViewFlipperChild(ViewFlipper viewFlipper) {
        if(!buildingViewModel.getIsBuildingSelected().getValue()) {
            viewFlipper.setDisplayedChild(LocalizationViewState.NO_BUILDING_SELECTED.getIndex());
        } else if(buildingViewModel.getRooms().getValue() != null && buildingViewModel.getRooms().getValue().isEmpty()) {
            viewFlipper.setDisplayedChild(LocalizationViewState.NO_ROOMS.getIndex());
        } else if (localizationViewModel.isLocalizationDone().getValue() != null && !localizationViewModel.isLocalizationDone().getValue()) {
            viewFlipper.setDisplayedChild(LocalizationViewState.LOCALIZATION_IN_PROGRESS.getIndex());
        } else {
            viewFlipper.setDisplayedChild(LocalizationViewState.LOCALIZED_ROOMS.getIndex());
        }
    }

    private void initializeButtons() {
        ImageButton showWifiButton = binding.localizationShowWifiButton;
        ImageButton locateNowButton = binding.localizationRefreshButton;
        locateNowButton.setOnClickListener(v -> localizationViewModel.refreshLocalizationNow());
        showWifiButton.setOnClickListener(v -> showWifiBottomSheetDialog());
    }

    private void initializeCountdownTimer() {
        TextView countdownTimerNumber = binding.localizationCountdownTimer;
        // localization countdown timer - show seconds
        localizationViewModel.getCountdownTime().observe(getViewLifecycleOwner(), secondsLeft -> countdownTimerNumber.setText(String.valueOf(secondsLeft)));
        countdownTimerNumber.setOnClickListener(v -> pauseOrResumeCountdown());
    }

    private void initializeWifiListAdapter() {
        List<ScanResult> wifiList = localizationViewModel.getWifiScanResults().getValue();
        if(wifiList == null) {
            wifiList = new ArrayList<>();
        }
        wifiListAdapter = new WifiListAdapter(wifiList);

        // update wifi list after every scan
        localizationViewModel.getWifiScanResults().observe(getViewLifecycleOwner(), scanResults -> wifiListAdapter.updateWifiList(scanResults));
    }

    private void initializeLocationSortedRoomAdapter() {
        RecyclerView roomsRecyclerView = binding.locationSortedRoomsList;
        locationSortedRoomAdapter = new LocationSortedRoomAdapter(LocalizationFragment.this);
        roomsRecyclerView.setAdapter(locationSortedRoomAdapter);
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // update rooms after every localization
        localizationViewModel.getLocationSortedRooms().observe(getViewLifecycleOwner(), localizedRooms -> locationSortedRoomAdapter.updateRooms(localizedRooms));
    }

    private void showWifiBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_wifi_list, null);
        bottomSheetDialog.setContentView(view);

        RecyclerView wifiListRecyclerView = view.findViewById(R.id.wifi_list_recycler_view);
        wifiListRecyclerView.setAdapter(wifiListAdapter);
        wifiListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        TextView countdownTimerNumber = view.findViewById(R.id.localization_second_countdown_timer);
        localizationViewModel.getCountdownTime().observe(getViewLifecycleOwner(), secondsLeft -> countdownTimerNumber.setText(String.valueOf(secondsLeft)));
        countdownTimerNumber.setOnClickListener(v -> pauseOrResumeCountdown());

        ImageButton locateNowButton = view.findViewById(R.id.localization_second_refresh_button);
        locateNowButton.setOnClickListener(v -> localizationViewModel.refreshLocalizationNow());

        ViewFlipper viewFlipper = view.findViewById(R.id.wifi_list_view_flipper);
        final int LOCALIZATION_IN_PROGRESS_STATE = 0;
        final int WIFI_LIST_STATE = 1;
        localizationViewModel.isLocalizationDone().observe(getViewLifecycleOwner(), isLocalizationDone -> viewFlipper.setDisplayedChild(isLocalizationDone? WIFI_LIST_STATE : LOCALIZATION_IN_PROGRESS_STATE));

        bottomSheetDialog.show();
    }

    private void pauseOrResumeCountdown() {
        if(localizationViewModel.isCountdownPaused()) {
            localizationViewModel.resumeCountdown();
            Toast.makeText(requireContext(), getString(R.string.toast_countdown_timer_resumed), Toast.LENGTH_SHORT).show();
        } else {
            localizationViewModel.pauseCountdown();
            Toast.makeText(requireContext(), getString(R.string.toast_countdown_timer_paused), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        localizationViewModel.stopCountdownTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        localizationViewModel.restartCountdown();
    }
}
