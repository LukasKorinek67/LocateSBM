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

import com.korinek.indoorlocalizatorapp.R;
import com.korinek.indoorlocalizatorapp.databinding.FragmentBuildingBinding;
import com.korinek.indoorlocalizatorapp.model.Room;



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
        final TextView textBuildingNotSet = binding.textBuildingNotSet;
        final TextView textBuildingName = binding.textBuildingName;
        final TextView textNoRooms = binding.textNoRooms;
        RecyclerView roomsRecyclerView = binding.recyclerViewRooms;
        Button addRoomButton = binding.addRoomButton;

        addRoomButton.setOnClickListener(v -> {
            showAddRoomDialog();
        });

        RoomAdapter roomAdapter = new RoomAdapter(new RoomAdapter.RoomActionListener() {
            @Override
            public void onRoomClick(Room room) {
                System.out.println("Room click: " + room.getName());
                Toast.makeText(requireContext(), "Click: " + room.getName(), Toast.LENGTH_LONG).show();
                // TODO - implement what to do
            }

            @Override
            public void onRoomCalibrate(Room room) {
                System.out.println("Room calibrate click: " + room.getName());
                Toast.makeText(requireContext(), "Click: " + room.getName() + " - Kalibrovat", Toast.LENGTH_LONG).show();
                // TODO - implement what to do
            }

            @Override
            public void onRoomEdit(Room room) {
                System.out.println("Room edit click: " + room.getName());
                Toast.makeText(requireContext(), "Click: " + room.getName() + " - Upravit", Toast.LENGTH_LONG).show();
                // TODO - implement what to do
            }

            @Override
            public void onRoomDelete(Room room) {
                System.out.println("Room delete click: " + room.getName());

                new AlertDialog.Builder(requireContext())
                        .setTitle("Odstranění místnosti")
                        .setMessage(String.format("Opravdu chcete odstranit místnost %s?", room.getName()))
                        //.setMessage(message)
                        .setPositiveButton("Odstranit", (dialog, which) -> {
                            buildingViewModel.deleteRoom(room);
                        })
                        .setNegativeButton("Zrušit", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .setOnCancelListener(DialogInterface::dismiss)
                        .show();
            }
        });

        buildingViewModel.getRooms().observe(getViewLifecycleOwner(), rooms -> {
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

    private void showAddRoomDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_room, null);

        EditText roomNameInput = dialogView.findViewById(R.id.room_name_input);
        Spinner iconSelector = dialogView.findViewById(R.id.room_icon_selector);

        initializeIconSelector(iconSelector);

        new AlertDialog.Builder(requireContext())
                .setTitle("Přidat novou místnost")
                .setView(dialogView)
                .setPositiveButton("Přidat", (dialog, which) -> {
                    String roomName = roomNameInput.getText().toString().trim();

                    if (!roomName.isEmpty() && selectedIcon != 0) {
                        int buildingId = buildingViewModel.getSelectedBuilding().getId();
                        Room room = new Room(roomName, buildingId, selectedIcon);
                        buildingViewModel.insertRoom(room);
                    } else {
                        Toast.makeText(requireContext(), "Chyba: Je třeba zadat název místnosti!", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Zrušit", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void initializeIconSelector(Spinner iconSelector) {
        int[] icons = {
                R.drawable.ic_room_pc_desktop,
                R.drawable.ic_room_meeting_board,
                R.drawable.ic_room_floor_plan,
                R.drawable.ic_room_sofa,
                R.drawable.ic_room_bed,
                R.drawable.ic_room_kitchen_faucet,
                R.drawable.ic_room_fridge,
                R.drawable.ic_room_shower,
                R.drawable.ic_room_toilet
        };
        ArrayAdapter<Integer> selectorAdapter = new ArrayAdapter<Integer>(requireContext(), android.R.layout.simple_spinner_item) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_room_icon_item, parent, false);
                }
                ImageView imageView = convertView.findViewById(R.id.spinner_icon);
                imageView.setImageResource(icons[position]);
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
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