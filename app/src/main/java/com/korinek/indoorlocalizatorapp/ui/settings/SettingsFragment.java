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
import androidx.lifecycle.ViewModelProvider;
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

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    SharedPreferencesHelper sharedPreferencesHelper;
    BuildingViewModel2 buildingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        sharedPreferencesHelper = new SharedPreferencesHelper(requireContext());
        buildingViewModel = new ViewModelProvider(this).get(BuildingViewModel2.class);

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
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_building_selection, null);
        bottomSheetDialog.setContentView(view);

        RecyclerView buildingsRecyclerView = view.findViewById(R.id.building_recycler_view);
        Button addBuildingButton = view.findViewById(R.id.add_building_button);

        BuildingAdapter buildingAdapter = new BuildingAdapter(new BuildingAdapter.BuildingActionListener() {
            @Override
            public void onBuildingClick(Building building) {
                setBuildingAndColor(building);
            }

            @Override
            public void onBuildingSwiped(Building building) {
                // delete building
                buildingViewModel.deleteBuilding(building);
                if(building.equals(sharedPreferencesHelper.getBuilding())) {
                    sharedPreferencesHelper.removeBuilding();
                    requireActivity().recreate();
                }
            }
        });

        buildingViewModel.getBuildings().observe(getViewLifecycleOwner(), buildings -> {
            Building selectedBuilding = sharedPreferencesHelper.getBuilding();
            buildingAdapter.updateBuildings(buildings, selectedBuilding);
        });

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
                Building building = buildingAdapter.getBuildingAt(position);
                //String message = getString(R.string.delete_building_message, building.getName());

                new AlertDialog.Builder(requireContext())
                        .setTitle("Odstranění objektu")
                        .setMessage(String.format("Opravdu chcete odstranit objekt %s? Všechna data budou smazána.", building.getName()))
                        //.setMessage(message)
                        .setPositiveButton("Odstranit", (dialog, which) -> {
                            buildingAdapter.getListener().onBuildingSwiped(building);
                        })
                        .setNegativeButton("Zrušit", (dialog, which) -> {
                            // return the building back
                            buildingAdapter.notifyItemChanged(position);
                        })
                        .setOnCancelListener(dialog -> {
                            // return the building back
                            buildingAdapter.notifyItemChanged(position);
                        })
                        .show();
            }
        }).attachToRecyclerView(buildingsRecyclerView);

        addBuildingButton.setOnClickListener(v -> {
            showAddBuildingDialog();
        });

        bottomSheetDialog.show();
    }

    private void setBuildingAndColor(Building building) {
        sharedPreferencesHelper.saveBuilding(building);
        sharedPreferencesHelper.saveTheme(building.getColor());
        requireActivity().recreate();
    }

    private void showAddBuildingDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_building, null);

        EditText buildingNameInput = dialogView.findViewById(R.id.building_name_input);
        RadioGroup colorPickerGroup = dialogView.findViewById(R.id.color_picker_group);

        new AlertDialog.Builder(requireContext())
                .setTitle("Přidat nový objekt")
                .setView(dialogView)
                .setPositiveButton("Přidat", (dialog, which) -> {
                    String buildingName = buildingNameInput.getText().toString().trim();
                    int selectedColor = getSelectedColor(colorPickerGroup);

                    if (!buildingName.isEmpty() && selectedColor != -1) {
                        Building building = new Building(buildingName, selectedColor);
                        buildingViewModel.insertBuilding(building);
                    } else {
                        Toast.makeText(requireContext(), "Chyba: Je třeba zadat název objektu a vybrat barvu!", Toast.LENGTH_LONG).show();
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
