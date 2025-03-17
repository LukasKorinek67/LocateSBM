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
        ImageButton showWifiButton = binding.localizationShowWifiButton;
        ImageButton locateNowButton = binding.localizationRefreshButton;
        TextView countdownTimerNumber = binding.localizationCountdownTimer;
        TextView textBuildingNotSet = binding.infoTextBuildingNotSetLocalization;
        TextView textNoRooms = binding.infoTextNoRoomsLocalization;
        RecyclerView roomsRecyclerView = binding.locationSortedRoomsList;

        // get all rooms
        buildingViewModel.getRooms().observe(getViewLifecycleOwner(), rooms -> {
            localizationViewModel.setLocationSortedRooms(rooms);
            if(rooms.isEmpty()) {
                textNoRooms.setVisibility(View.VISIBLE);
                roomsRecyclerView.setVisibility(View.GONE);
            } else {
                textNoRooms.setVisibility(View.GONE);
                roomsRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        // init wifi list adapter
        List<ScanResult> wifiList = localizationViewModel.getWifiScanResults().getValue();
        if(wifiList == null) {
            wifiList = new ArrayList<>();
        }
        wifiListAdapter = new WifiListAdapter(wifiList);


        // init location sorted room adapter
        locationSortedRoomAdapter = new LocationSortedRoomAdapter(LocalizationFragment.this);
        roomsRecyclerView.setAdapter(locationSortedRoomAdapter);
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // visibility of elements depends on selected building
        buildingViewModel.getIsBuildingSelected().observe(getViewLifecycleOwner(), isBuildingSelected -> {
            roomsRecyclerView.setVisibility(isBuildingSelected ? View.VISIBLE : View.GONE);
            textBuildingNotSet.setVisibility(isBuildingSelected ? View.GONE : View.VISIBLE);
            locateNowButton.setVisibility(isBuildingSelected ? View.VISIBLE : View.GONE);
            countdownTimerNumber.setVisibility(isBuildingSelected ? View.VISIBLE : View.GONE);
            if (!isBuildingSelected) {
                textNoRooms.setVisibility(View.INVISIBLE);
            }
        });


        // localization countdown timer - show seconds
        localizationViewModel.getCountdownTime().observe(getViewLifecycleOwner(), secondsLeft -> countdownTimerNumber.setText(String.valueOf(secondsLeft)));

        // update wifi list after every scan
        localizationViewModel.getWifiScanResults().observe(getViewLifecycleOwner(), scanResults -> {
            wifiListAdapter.updateWifiList(scanResults);
        });

        // update rooms after every localization
        localizationViewModel.getLocationSortedRooms().observe(getViewLifecycleOwner(), rooms -> {
            locationSortedRoomAdapter.updateRooms(rooms);
        });

        // buttons
        locateNowButton.setOnClickListener(v -> localizationViewModel.refreshLocalizationNow());
        showWifiButton.setOnClickListener(v -> showWifiBottomSheetDialog());
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

        bottomSheetDialog.show();
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
