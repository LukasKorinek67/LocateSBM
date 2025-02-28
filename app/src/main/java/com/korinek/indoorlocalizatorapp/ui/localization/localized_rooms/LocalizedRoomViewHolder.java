package com.korinek.indoorlocalizatorapp.ui.localization.localized_rooms;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.korinek.indoorlocalizatorapp.R;
import com.korinek.indoorlocalizatorapp.model.Room;
import com.korinek.indoorlocalizatorapp.model.RoomApiModel;
import com.korinek.indoorlocalizatorapp.utils.RoomAttributesFilter;
import com.korinek.indoorlocalizatorapp.utils.api.ApiCalls;
import com.korinek.indoorlocalizatorapp.utils.api.RequestHandler;

import java.util.HashMap;
import java.util.Map;

class LocalizedRoomViewHolder extends LocationSortedRoomAdapter.LocationSortedRoomViewHolder {
    private final ImageView roomIcon;
    private final TextView roomNameTextView;
    private final Button roomSetButton;
    private final ProgressBar overviewLoadingBar;
    private final TextView roomDataNotAvailableText;
    private final RecyclerView recyclerView;
    RoomDataOverviewAdapter adapter;

    public LocalizedRoomViewHolder(@NonNull View itemView) {
        super(itemView);
        roomIcon = itemView.findViewById(R.id.localized_room_icon);
        roomNameTextView = itemView.findViewById(R.id.localized_room_name);
        roomSetButton = itemView.findViewById(R.id.room_set_button);
        overviewLoadingBar = itemView.findViewById(R.id.data_overview_loading_bar);
        roomDataNotAvailableText = itemView.findViewById(R.id.text_room_data_not_available);
        recyclerView = itemView.findViewById(R.id.room_data_overview_recycler_view);
    }

    public void bind(Room room, Fragment parentFragment) {
        roomIcon.setImageResource(room.getIcon());
        roomNameTextView.setText(room.getName());


        roomSetButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("roomName", room.getName());

            NavController navController = Navigation.findNavController(parentFragment.requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_localizationFragment_to_roomSetupFragment, bundle);
        });

        overviewLoadingBar.setVisibility(View.VISIBLE);
        roomDataNotAvailableText.setVisibility(View.GONE);
        Map<String, Object> attributes = new HashMap<>();

        adapter = new RoomDataOverviewAdapter(attributes);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));

        // Sdílený ViewPool pro optimalizaci recyklace
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);

        loadData(room.getName());
    }

    private void loadData(String roomName) {
        RequestHandler requestHandler = new RequestHandler(itemView.getContext());

        if(roomName != null) {
            requestHandler.getRoomData(roomName, new ApiCalls.RoomDataCallback() {
                @Override
                public void onSuccess(RoomApiModel room) {
                    Map<String, Object> attributes = RoomAttributesFilter.filterNegativeAttributes(room.getAttributes());
                    adapter.updateAttributes(attributes);
                    overviewLoadingBar.setVisibility(View.GONE);
                    roomDataNotAvailableText.setVisibility(View.GONE);
                    roomSetButton.setEnabled(true);
                }

                @Override
                public void onFailure(String errorMessage) {
                    overviewLoadingBar.setVisibility(View.GONE);
                    roomDataNotAvailableText.setVisibility(View.VISIBLE);
                    roomSetButton.setEnabled(false);
                    Toast.makeText(itemView.getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
