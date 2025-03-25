package com.korinek.locate_sbm.ui.localization.localized_rooms;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.TooltipCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.korinek.locate_sbm.R;
import com.korinek.locate_sbm.model.LocalizedRoom;
import com.korinek.locate_sbm.model.api.RoomApiModel;
import com.korinek.locate_sbm.utils.RoomAttributesHelper;
import com.korinek.locate_sbm.utils.RoomIconsHelper;
import com.korinek.locate_sbm.utils.api.ApiCalls;
import com.korinek.locate_sbm.utils.api.RequestHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

class LocalizedRoomViewHolder extends LocationSortedRoomAdapter.LocationSortedRoomViewHolder {
    private final int MAX_ATTRIBUTES_TO_SHOW = 4;
    Map<String, Object> allAttributes;
    private final ImageView roomIcon;
    private final TextView roomNameTextView;
    private final Button roomSetButton;
    private final TextView roomLocationProbabilityTextView;
    private final ProgressBar overviewLoadingBar;
    private final TextView roomDataNotAvailableText;
    private final TextView roomHasNoAttributesText;
    private final ImageView indicatorMoreItems;
    private final RecyclerView recyclerView;
    RoomDataOverviewAdapter adapter;

    public LocalizedRoomViewHolder(@NonNull View itemView) {
        super(itemView);
        roomIcon = itemView.findViewById(R.id.localized_room_icon);
        roomNameTextView = itemView.findViewById(R.id.localized_room_name);
        roomSetButton = itemView.findViewById(R.id.room_set_button);
        roomLocationProbabilityTextView = itemView.findViewById(R.id.localized_location_probability);
        overviewLoadingBar = itemView.findViewById(R.id.data_overview_loading_bar);
        roomDataNotAvailableText = itemView.findViewById(R.id.text_room_data_not_available);
        roomHasNoAttributesText = itemView.findViewById(R.id.text_room_has_no_attributes);
        indicatorMoreItems = itemView.findViewById(R.id.indicator_more_items);
        recyclerView = itemView.findViewById(R.id.room_data_overview_recycler_view);
    }

    public void bind(LocalizedRoom localizedRoom, Fragment parentFragment) {
        roomIcon.setImageResource(RoomIconsHelper.getIconResId(localizedRoom.getRoom().getIcon()));
        roomNameTextView.setText(localizedRoom.getRoom().getName());
        roomLocationProbabilityTextView.setText(String.format(Locale.getDefault(),"%.0f %%", localizedRoom.getLocationProbability()));

        // Tooltip text on roomLocationProbabilityTextView
        String percentageText = roomLocationProbabilityTextView.getText().toString();
        TooltipCompat.setTooltipText(
                roomLocationProbabilityTextView,
                String.format(Locale.getDefault(), parentFragment.getString(R.string.tooltip_localization_probability), percentageText)
        );
        // show tooltip on click instead of long click
        roomLocationProbabilityTextView.setOnClickListener(View::performLongClick);

        roomSetButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("roomName", localizedRoom.getRoom().getName());
            bundle.putString("roomIcon", localizedRoom.getRoom().getIcon());

            NavController navController = Navigation.findNavController(parentFragment.requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_localizationFragment_to_roomSetupFragment, bundle);
        });

        overviewLoadingBar.setVisibility(View.VISIBLE);
        roomDataNotAvailableText.setVisibility(View.GONE);
        roomHasNoAttributesText.setVisibility(View.GONE);
        indicatorMoreItems.setVisibility(View.GONE);
        Map<String, Object> attributes = new HashMap<>();

        adapter = new RoomDataOverviewAdapter(attributes);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));

        // Sdílený ViewPool pro optimalizaci recyklace
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);

        loadData(localizedRoom.getRoom().getName());

        indicatorMoreItems.setOnClickListener(v -> {
            indicatorMoreItems.setVisibility(View.GONE);
            if(allAttributes != null) {
                adapter.updateAttributes(allAttributes);
            }
        });
    }

    private void loadData(String roomName) {
        RequestHandler requestHandler = new RequestHandler(itemView.getContext());

        if(roomName != null) {
            requestHandler.getRoomData(roomName, new ApiCalls.RoomDataCallback() {
                @Override
                public void onSuccess(RoomApiModel room) {
                    allAttributes = RoomAttributesHelper.filterNegativeAttributes(room.getAttributes());
                    allAttributes = RoomAttributesHelper.sortAttributes(allAttributes);

                    if (allAttributes.isEmpty()) {
                        roomHasNoAttributesText.setVisibility(View.VISIBLE);
                    } else if (allAttributes.size() > MAX_ATTRIBUTES_TO_SHOW) {
                        Map<String, Object> shortenAttributes = allAttributes.entrySet()
                                .stream()
                                .limit(MAX_ATTRIBUTES_TO_SHOW)
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                        (e1, e2) -> e1,
                                        LinkedHashMap::new
                                ));
                        indicatorMoreItems.setVisibility(View.VISIBLE);
                        adapter.updateAttributes(shortenAttributes);
                        roomHasNoAttributesText.setVisibility(View.GONE);
                    } else {
                        indicatorMoreItems.setVisibility(View.GONE);
                        adapter.updateAttributes(allAttributes);
                        roomHasNoAttributesText.setVisibility(View.GONE);
                    }
                    overviewLoadingBar.setVisibility(View.GONE);
                    roomDataNotAvailableText.setVisibility(View.GONE);
                    roomSetButton.setEnabled(true);
                }

                @Override
                public void onFailure(String errorMessage) {
                    overviewLoadingBar.setVisibility(View.GONE);
                    roomDataNotAvailableText.setVisibility(View.VISIBLE);
                    roomHasNoAttributesText.setVisibility(View.GONE);
                    indicatorMoreItems.setVisibility(View.GONE);
                    roomSetButton.setEnabled(false);
                    Toast.makeText(itemView.getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
