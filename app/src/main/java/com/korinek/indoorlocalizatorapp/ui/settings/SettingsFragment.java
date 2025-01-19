package com.korinek.indoorlocalizatorapp.ui.settings;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.korinek.indoorlocalizatorapp.R;
import com.korinek.indoorlocalizatorapp.databinding.FragmentSettingsBinding;
import com.korinek.indoorlocalizatorapp.model.Building;
import com.korinek.indoorlocalizatorapp.ui.building.BuildingAdapter;
import com.korinek.indoorlocalizatorapp.utils.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    SharedPreferencesHelper sharedPreferencesHelper;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        sharedPreferencesHelper = new SharedPreferencesHelper(requireContext());

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_fragment_container, new SettingsPreferenceFragment())
                .commit();

        initializeBuildingSelector();

        return root;
    }

    private void initializeBuildingSelector() {
        TextView buildingName = binding.buildingName;
        View buildingColorView = binding.buildingColorView;
        GradientDrawable drawable = (GradientDrawable) buildingColorView.getBackground();


        if(sharedPreferencesHelper.isBuildingSelected()) {
            Building selectedBuilding = sharedPreferencesHelper.getBuilding();
            buildingName.setText(selectedBuilding.getName());
            buildingColorView.setBackgroundColor(selectedBuilding.getColor());
            drawable.setColor(ContextCompat.getColor(requireContext(), selectedBuilding.getColor()));
            buildingColorView.setBackground(drawable);
        } else {
            buildingName.setText("Není zvoleno");
            drawable.setColor(ContextCompat.getColor(requireContext(), R.color.white));
            buildingColorView.setBackground(drawable);
        }

        LinearLayout buildingSelector = binding.buildingSelector;
        buildingSelector.setOnClickListener(v -> showBuildingBottomSheet(buildingName));

    }

    private void showBuildingBottomSheet(TextView buildingName) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_building_selection, null);
        bottomSheetDialog.setContentView(view);

        RecyclerView buildingsRecyclerView = view.findViewById(R.id.building_recycler_view);
        Button addBuildingButton = view.findViewById(R.id.add_building_button);

        List<Building> buildings = getBuildings();
        BuildingAdapter buildingAdapter = getBuildingAdapter(buildings);

        buildingsRecyclerView.setAdapter(buildingAdapter);
        buildingsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(buildingsRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        buildingsRecyclerView.addItemDecoration(dividerItemDecoration);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Building building = buildings.get(position);
                buildingAdapter.getListener().onBuildingSwiped(building);
                buildingAdapter.notifyItemRemoved(position);
            }
        }).attachToRecyclerView(buildingsRecyclerView);

        addBuildingButton.setOnClickListener(v -> {
            showAddBuildingDialog(buildings, buildingAdapter);
        });

        bottomSheetDialog.show();
    }

    @NonNull
    private BuildingAdapter getBuildingAdapter(List<Building> buildings) {
        Building selectedBuilding = sharedPreferencesHelper.getBuilding();
        return new BuildingAdapter(buildings, selectedBuilding, new BuildingAdapter.BuildingActionListener() {
            @Override
            public void onBuildingClick(Building building) {
                setBuildingAndColor(building);
            }

            @Override
            public void onBuildingSwiped(Building building) {
                System.out.println("Odstranění objektu " + building.getName());
                buildings.remove(building);
                //adapter.notifyDataSetChanged();
            }
        });
    }

    private List<Building> getBuildings() {
        // Simulace dat
        List<Building> buildings = new ArrayList<>();
        buildings.add(new Building("TUL", R.color.purple));
        buildings.add(new Building("Domov", R.color.blue));
        buildings.add(new Building("Barák rodiče", R.color.orange));
        buildings.add(new Building("Byt 1", R.color.green));
        buildings.add(new Building("Byt 2", R.color.pink));
        buildings.add(new Building("Byt 3", R.color.red));
        buildings.add(new Building("Byt 4", R.color.yellow));
        buildings.add(new Building("Byt 5", R.color.black));
        return buildings;
    }

    private void setBuildingAndColor(Building building) {
        sharedPreferencesHelper.saveBuilding(building);
        sharedPreferencesHelper.saveTheme(building.getColor());
        requireActivity().recreate();
    }

    private void showAddBuildingDialog(List<Building> buildings, BuildingAdapter buildingAdapter) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_building, null);

        EditText buildingNameInput = dialogView.findViewById(R.id.building_name_input);
        RadioGroup colorPickerGroup = dialogView.findViewById(R.id.color_picker_group);

        new AlertDialog.Builder(requireContext())
                .setTitle("Přidat novou budovu")
                .setView(dialogView)
                .setPositiveButton("Přidat", (dialog, which) -> {
                    String buildingName = buildingNameInput.getText().toString().trim();
                    int selectedColor = getSelectedColor(colorPickerGroup);

                    if (!buildingName.isEmpty() && selectedColor != -1) {
                        buildings.add(new Building(buildingName, selectedColor));
                        buildingAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(requireContext(), "Chyba: Je třeba zadat název budovy a vybrat barvu!", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Zrušit", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private int getSelectedColor(RadioGroup colorPickerGroup) {
        int selectedId = colorPickerGroup.getCheckedRadioButtonId();

        if (selectedId == R.id.color_black) {
            return R.color.black;
        } else if (selectedId == R.id.color_blue) {
            return R.color.blue;
        } else if (selectedId == R.id.color_green) {
            return R.color.green;
        } else if (selectedId == R.id.color_orange) {
            return R.color.orange;
        } else if (selectedId == R.id.color_pink) {
            return R.color.pink;
        } else if (selectedId == R.id.color_purple) {
            return R.color.purple;
        } else if (selectedId == R.id.color_red) {
            return R.color.red;
        } else if (selectedId == R.id.color_yellow) {
            return R.color.yellow;
        }
        return -1;
    }
}
