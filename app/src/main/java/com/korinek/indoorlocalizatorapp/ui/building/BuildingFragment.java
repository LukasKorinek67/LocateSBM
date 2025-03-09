package com.korinek.indoorlocalizatorapp.ui.building;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.korinek.indoorlocalizatorapp.R;
import com.korinek.indoorlocalizatorapp.databinding.FragmentBuildingBinding;
import com.korinek.indoorlocalizatorapp.model.Room;
import com.korinek.indoorlocalizatorapp.utils.RoomIconsHelper;

import java.util.Comparator;


public class BuildingFragment extends Fragment {

    private FragmentBuildingBinding binding;
    BuildingViewModel buildingViewModel;
    private int selectedIcon;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        buildingViewModel = new ViewModelProvider(requireActivity()).get(BuildingViewModel.class);

        binding = FragmentBuildingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initializeComponents();

        return root;
    }

    private void initializeComponents() {
        final TextView textBuildingNotSet = binding.infoTextBuildingNotSetBuilding;
        final TextView textBuildingName = binding.textBuildingName;
        final TextView textNoRooms = binding.infoTextNoRoomsBuilding;
        RecyclerView roomsRecyclerView = binding.recyclerViewRooms;
        Button addRoomButton = binding.addRoomButton;

        addRoomButton.setOnClickListener(v -> showAddRoomBottomSheet());

        RoomAdapter roomAdapter = new RoomAdapter(new RoomAdapter.RoomActionListener() {
            @Override
            public void onRoomClick(Room room) {
                // TODO - implement what to do
            }

            @Override
            public void onRoomCalibrate(Room room) {
                Toast.makeText(requireContext(), "Not implemented yet", Toast.LENGTH_LONG).show();
                // TODO - implement what to do
            }

            @Override
            public void onRoomEdit(Room room) {
                showEditRoomBottomSheet(room);
            }

            @Override
            public void onRoomDelete(Room room) {
                new AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.dialog_title_delete_room))
                        .setMessage(String.format(getString(R.string.dialog_message_delete_room), room.getName()))
                        .setPositiveButton(getString(R.string.delete), (dialog, which) -> buildingViewModel.deleteRoom(room))
                        .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                        .setOnCancelListener(DialogInterface::dismiss)
                        .show();
            }
        });

        buildingViewModel.getRooms().observe(getViewLifecycleOwner(), rooms -> {
            // sort rooms by name
            rooms.sort(Comparator.comparing(Room::getName, String.CASE_INSENSITIVE_ORDER));
            roomAdapter.updateRooms(rooms);
            if(rooms.isEmpty()) {
                textNoRooms.setVisibility(View.VISIBLE);
            } else {
                textNoRooms.setVisibility(View.GONE);
            }
        });

        roomsRecyclerView.setAdapter(roomAdapter);
        roomsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        buildingViewModel.getIsBuildingSelected().observe(getViewLifecycleOwner(), isBuildingSelected -> {
            textBuildingNotSet.setVisibility(isBuildingSelected ? View.GONE : View.VISIBLE);
            textBuildingName.setVisibility(isBuildingSelected ? View.VISIBLE : View.GONE);
            roomsRecyclerView.setVisibility(isBuildingSelected ? View.VISIBLE : View.GONE);
            addRoomButton.setVisibility(isBuildingSelected ? View.VISIBLE : View.GONE);

            if (isBuildingSelected) {
                if ((buildingViewModel.getSelectedBuilding() != null)) {
                    textBuildingName.setText(buildingViewModel.getSelectedBuilding().getName());
                }

            } else {
                textNoRooms.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void showAddRoomBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_add_room, null);
        bottomSheetDialog.setContentView(view);

        EditText roomNameInput = view.findViewById(R.id.room_name_input);
        Spinner iconSelector = view.findViewById(R.id.room_icon_selector);
        Button addButton = view.findViewById(R.id.confirm_add_room_button);

        initializeIconSelector(iconSelector);
        addButton.setOnClickListener(v -> {
            String roomName = roomNameInput.getText().toString().trim();

            if (!roomName.isEmpty() && selectedIcon != 0) {
                int buildingId = buildingViewModel.getSelectedBuilding().getId();
                Room room = new Room(roomName, buildingId, RoomIconsHelper.getIconName(selectedIcon));
                buildingViewModel.insertRoom(room);
                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(requireContext(), getString(R.string.dialog_error_add_room), Toast.LENGTH_LONG).show();
            }
        });

        bottomSheetDialog.show();
    }

    private void showEditRoomBottomSheet(Room room) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_edit_room, null);
        bottomSheetDialog.setContentView(view);

        TextView roomName = view.findViewById(R.id.room_name_edit_bottom_sheet);
        Spinner iconSelector = view.findViewById(R.id.room_edit_icon_selector);
        Button editButton = view.findViewById(R.id.confirm_edit_room_button);

        roomName.setText(room.getName());
        initializeIconSelector(iconSelector, room.getIcon());

        editButton.setOnClickListener(v -> {
            if (selectedIcon != 0) {
                room.setIcon(RoomIconsHelper.getIconName(selectedIcon));
                buildingViewModel.updateRoom(room);
                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(requireContext(), getString(R.string.dialog_error_edit_room), Toast.LENGTH_LONG).show();
            }
        });

        bottomSheetDialog.show();
    }

    private void initializeIconSelector(Spinner iconSelector) {
        initializeIconSelector(iconSelector, null);
    }
    private void initializeIconSelector(Spinner iconSelector, String defaultIconName) {
        int[] icons = RoomIconsHelper.getIcons();
        ArrayAdapter<Integer> selectorAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_room_icon_item, parent, false);
                }
                ImageView imageView = convertView.findViewById(R.id.spinner_icon);
                imageView.setImageResource(icons[position]);
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_room_icon_item, parent, false);
                }
                ImageView imageView = convertView.findViewById(R.id.spinner_icon);
                imageView.setImageResource(icons[position]);
                return convertView;
            }

            @Override
            public int getCount() {
                return icons.length;
            }
        };
        iconSelector.setAdapter(selectorAdapter);

        if(defaultIconName != null) {
            for (int i = 0; i < icons.length; i++) {
                if (icons[i] == RoomIconsHelper.getIconResId(defaultIconName)) {
                    iconSelector.setSelection(i);
                    selectedIcon = icons[i];
                }
            }
        }

        iconSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedIcon = icons[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Když není nic vybráno
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}