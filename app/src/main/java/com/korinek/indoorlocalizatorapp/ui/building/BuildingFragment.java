package com.korinek.indoorlocalizatorapp.ui.building;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.korinek.indoorlocalizatorapp.R;
import com.korinek.indoorlocalizatorapp.model.Building;
import com.korinek.indoorlocalizatorapp.databinding.FragmentBuildingBinding;
import com.korinek.indoorlocalizatorapp.utils.ColourToTheme;

import java.util.ArrayList;
import java.util.List;

public class BuildingFragment extends Fragment {

    private FragmentBuildingBinding binding;
    private List<Building> buildings = new ArrayList<>();
    private BuildingAdapter buildingAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BuildingViewModel buildingViewModel =
                new ViewModelProvider(this).get(BuildingViewModel.class);

        binding = FragmentBuildingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initializeAddButton();
        initializeList();

        //final TextView textView = binding.textBuilding;
        //buildingViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initializeAddButton() {
        Building building = new Building("TUL", R.color.purple);

        Button addButton = binding.addBuildingButton;
        addButton.setOnClickListener(v -> {
            Log.d("BuildingFragment", "Add building button clicked");
            changeColour(building);
        });
    }

    private void initializeList() {
        // mock up data
        buildings.add(new Building("TUL", R.color.purple));
        buildings.add(new Building("Domov", R.color.blue));
        buildings.add(new Building("Barák rodiče", R.color.orange));
        buildings.add(new Building("Byt 1", R.color.green));
        buildings.add(new Building("Byt 2", R.color.pink));
        buildings.add(new Building("Byt 3", R.color.red));
        buildings.add(new Building("Byt 4", R.color.yellow));
        buildings.add(new Building("Byt 5", R.color.black));

        RecyclerView buildingsRecyclerView = binding.buildingsList;
        buildingAdapter = new BuildingAdapter(buildings, building -> {
            Log.d("BuildingFragment", "Building clicked: " + building.getName());
            changeColour(building);
        });
        buildingsRecyclerView.setAdapter(buildingAdapter);
        buildingsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(buildingsRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        buildingsRecyclerView.addItemDecoration(dividerItemDecoration);

    }

    private void changeColour(Building building) {
        int theme = ColourToTheme.getThemeForColor(building.getColour());

        // save theme to SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selected_theme", theme);
        editor.apply();

        requireActivity().recreate();
    }
}