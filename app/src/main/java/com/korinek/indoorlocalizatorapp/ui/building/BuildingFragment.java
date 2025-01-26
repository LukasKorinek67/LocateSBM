package com.korinek.indoorlocalizatorapp.ui.building;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.korinek.indoorlocalizatorapp.R;
import com.korinek.indoorlocalizatorapp.databinding.FragmentBuildingBinding;
import com.korinek.indoorlocalizatorapp.model.Room;



public class BuildingFragment extends Fragment {

    private FragmentBuildingBinding binding;
    BuildingViewModel buildingViewModel;

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
            }

            @Override
            public void onRoomSwiped(Room room) {
                System.out.println("Room swiped: " + room.getName());
                buildingViewModel.deleteRoom(room);
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
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Room room = roomAdapter.getRoomAt(position);
                //String message = getString(R.string.delete_building_message, building.getName());

                new AlertDialog.Builder(requireContext())
                        .setTitle("Odstranění místnosti")
                        .setMessage(String.format("Opravdu chcete odstranit místnost %s?", room.getName()))
                        //.setMessage(message)
                        .setPositiveButton("Odstranit", (dialog, which) -> {
                            roomAdapter.getListener().onRoomSwiped(room);
                        })
                        .setNegativeButton("Zrušit", (dialog, which) -> {
                            // return the room back
                            roomAdapter.notifyItemChanged(position);
                        })
                        .setOnCancelListener(dialog -> {
                            // return the room back
                            roomAdapter.notifyItemChanged(position);
                        })
                        .show();
            }
        }).attachToRecyclerView(roomsRecyclerView);

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

        new AlertDialog.Builder(requireContext())
                .setTitle("Přidat novou místnost")
                .setView(dialogView)
                .setPositiveButton("Přidat", (dialog, which) -> {
                    String roomName = roomNameInput.getText().toString().trim();

                    if (!roomName.isEmpty()) {
                        int buildingId = buildingViewModel.getSelectedBuilding().getId();
                        Room room = new Room(roomName, buildingId);
                        buildingViewModel.insertRoom(room);
                    } else {
                        Toast.makeText(requireContext(), "Chyba: Je třeba zadat název místnosti!", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Zrušit", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}