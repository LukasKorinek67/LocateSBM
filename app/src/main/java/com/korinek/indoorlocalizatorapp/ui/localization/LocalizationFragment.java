package com.korinek.indoorlocalizatorapp.ui.localization;

import android.Manifest;
import android.content.pm.PackageManager;
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

import com.korinek.indoorlocalizatorapp.databinding.FragmentLocalizationBinding;
import com.korinek.indoorlocalizatorapp.model.Room;
import com.korinek.indoorlocalizatorapp.ui.building.BuildingViewModel;
import com.korinek.indoorlocalizatorapp.ui.localization.localized_rooms.LocationSortedRoomAdapter;

import java.util.List;

public class LocalizationFragment extends Fragment {

    private FragmentLocalizationBinding binding;
    LocalizationCountdownViewModel localizationViewModel;
    BuildingViewModel buildingViewModel;
    LocationSortedRoomAdapter locationSortedRoomAdapter;

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
        ImageButton locateNowButton = binding.localizationRefreshButton;
        TextView countdownTimerNumber = binding.localizationCountdownTimer;
        TextView textBuildingNotSet = binding.infoTextBuildingNotSetLocalization;
        TextView textNoRooms = binding.infoTextNoRoomsLocalization;
        RecyclerView roomsRecyclerView = binding.locationSortedRoomsList;

        // localization countdown timer
        localizationViewModel.getCountdownTime().observe(getViewLifecycleOwner(), secondsLeft -> countdownTimerNumber.setText(String.valueOf(secondsLeft)));
        localizationViewModel.getLocalizationTrigger().observe(getViewLifecycleOwner(), trigger -> sortAndUpdateRoomsList(buildingViewModel.getRooms().getValue()));

        locateNowButton.setOnClickListener(v -> localizationViewModel.refreshLocalizationNow());

        locationSortedRoomAdapter = new LocationSortedRoomAdapter(LocalizationFragment.this);

        buildingViewModel.getRooms().observe(getViewLifecycleOwner(), rooms -> {
            sortAndUpdateRoomsList(rooms);
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
    }

    private void sortAndUpdateRoomsList(List<Room> rooms) {
        // Sorting rooms by location
        List<Room> locationSortedRooms = RoomLocationSorter.sortRoomsByLocation(rooms);
        locationSortedRoomAdapter.updateRooms(locationSortedRooms);
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
