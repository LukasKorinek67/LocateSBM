package com.korinek.indoorlocalizatorapp.ui.localization;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.korinek.indoorlocalizatorapp.R;
import com.korinek.indoorlocalizatorapp.databinding.FragmentLocalizationBinding;
import com.korinek.indoorlocalizatorapp.ui.building.BuildingViewModel;
import com.korinek.indoorlocalizatorapp.ui.localization.accelerometer.AccelerometerFragment;
import com.korinek.indoorlocalizatorapp.ui.localization.gyroscope.GyroscopeFragment;
import com.korinek.indoorlocalizatorapp.ui.localization.localized_rooms.LocationSortedRoomAdapter;
import com.korinek.indoorlocalizatorapp.ui.localization.wifi_analysis.WifiAnalysisFragment;

public class LocalizationFragment extends Fragment {

    private FragmentLocalizationBinding binding;
    BuildingViewModel buildingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        buildingViewModel = new ViewModelProvider(requireActivity()).get(BuildingViewModel.class);

        binding = FragmentLocalizationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // check and request for permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        //addFragmentsOnScreen();
        initializeRoomsList();

        return root;
    }

    private void initializeRoomsList() {
        final TextView textBuildingNotSet = binding.infoTextBuildingNotSetLocalization;
        final TextView textNoRooms = binding.infoTextNoRoomsLocalization;
        RecyclerView roomsRecyclerView = binding.locationSortedRoomsList;

        LocationSortedRoomAdapter locationSortedRoomAdapter = new LocationSortedRoomAdapter(LocalizationFragment.this);

        buildingViewModel.getRooms().observe(getViewLifecycleOwner(), rooms -> {
            locationSortedRoomAdapter.updateRooms(rooms);
            if(rooms.isEmpty()) {
                textNoRooms.setVisibility(View.VISIBLE);
                roomsRecyclerView.setVisibility(View.GONE);
            } else {
                textNoRooms.setVisibility(View.GONE);
                roomsRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        roomsRecyclerView.setAdapter(locationSortedRoomAdapter);
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        buildingViewModel.getIsBuildingSelected().observe(getViewLifecycleOwner(), isBuildingSelected -> {
            roomsRecyclerView.setVisibility(isBuildingSelected ? View.VISIBLE : View.GONE);
            textBuildingNotSet.setVisibility(isBuildingSelected ? View.GONE : View.VISIBLE);
            if (!isBuildingSelected) {
                textNoRooms.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /*private void addFragmentsOnScreen() {
        // WifiAnalysisFragment
        getChildFragmentManager().beginTransaction()
                .replace(R.id.wifi_analysis_container, new WifiAnalysisFragment())
                .commit();

        // GyroscopeFragment
        getChildFragmentManager().beginTransaction()
                .replace(R.id.gyroscope_container, new GyroscopeFragment())
                .commit();

        // AccelerometerFragment
        getChildFragmentManager().beginTransaction()
                .replace(R.id.accelerometer_container, new AccelerometerFragment())
                .commit();
    }*/
}
