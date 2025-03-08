package com.korinek.indoorlocalizatorapp.ui.settings;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
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
import com.korinek.indoorlocalizatorapp.ui.building.BuildingViewModel;
import com.korinek.indoorlocalizatorapp.utils.ColorHelper;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    BuildingViewModel buildingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        buildingViewModel = new ViewModelProvider(requireActivity()).get(BuildingViewModel.class);

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


        buildingViewModel.getIsBuildingSelected().observe(getViewLifecycleOwner(), isBuildingSelected -> {
            if(isBuildingSelected) {
                Building selectedBuilding = buildingViewModel.getSelectedBuilding();
                buildingName.setText(selectedBuilding.getName());
                drawable.setColor(ContextCompat.getColor(requireContext(), selectedBuilding.getColor()));
                buildingColorView.setBackground(drawable);
            } else {
                buildingName.setText(getText(R.string.not_selected));
                drawable.setColor(ContextCompat.getColor(requireContext(), R.color.white));
                buildingColorView.setBackground(drawable);
            }
        });

        CardView buildingSelector = binding.buildingSelector;
        buildingSelector.setOnClickListener(v -> showBuildingBottomSheet());

    }

    private void showBuildingBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_building_selection, null);
        bottomSheetDialog.setContentView(view);

        RecyclerView buildingsRecyclerView = view.findViewById(R.id.building_recycler_view);
        Button addBuildingButton = view.findViewById(R.id.add_building_button);

        BuildingAdapter buildingAdapter = new BuildingAdapter(new BuildingAdapter.BuildingActionListener() {
            @Override
            public void onBuildingClick(Building building) {
                buildingViewModel.setBuilding(building);
                requireActivity().recreate();
            }

            @Override
            public void onBuildingSwiped(Building building) {
                // delete building
                buildingViewModel.deleteBuilding(building);
                if(building.equals(buildingViewModel.getSelectedBuilding())) {
                    buildingViewModel.unselectBuilding();
                    requireActivity().recreate();
                }
            }
        });

        buildingViewModel.getAllBuildings().observe(getViewLifecycleOwner(), buildings -> {
            Building selectedBuilding = buildingViewModel.getSelectedBuilding();
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
                String message = String.format(Locale.getDefault(), getString(R.string.dialog_message_delete_building), building.getName());

                new AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.dialog_title_delete_building))
                        .setMessage(message)
                        .setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                            buildingAdapter.getListener().onBuildingSwiped(building);
                        })
                        .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
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

    private void showAddBuildingDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_building, null);

        EditText buildingNameInput = dialogView.findViewById(R.id.building_name_input);
        RadioGroup colorPickerGroup = dialogView.findViewById(R.id.color_picker_group);

        for (ColorHelper.ColorTheme colorTheme : ColorHelper.getAllColors()) {
            RadioButton radioButton = new RadioButton(requireContext());
            radioButton.setText(getString(colorTheme.getColorNameResId()));
            radioButton.setId(colorTheme.getColorId());
            radioButton.setButtonTintList(ContextCompat.getColorStateList(requireContext(), colorTheme.getColor()));

            colorPickerGroup.addView(radioButton);
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.dialog_title_add_building))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.add), (dialog, which) -> {
                    String buildingName = buildingNameInput.getText().toString().trim();
                    int selectedColor = getSelectedColor(colorPickerGroup);

                    if (!buildingName.isEmpty() && selectedColor != -1) {
                        Building building = new Building(buildingName, selectedColor);
                        buildingViewModel.insertBuilding(building);
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.dialog_error_add_building), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }

    private int getSelectedColor(RadioGroup colorPickerGroup) {
        int selectedId = colorPickerGroup.getCheckedRadioButtonId();
        for (ColorHelper.ColorTheme colorTheme : ColorHelper.getAllColors()) {
            if (selectedId == colorTheme.getColorId()) {
                return colorTheme.getColor();
            }
        }
        return -1;
    }
}
