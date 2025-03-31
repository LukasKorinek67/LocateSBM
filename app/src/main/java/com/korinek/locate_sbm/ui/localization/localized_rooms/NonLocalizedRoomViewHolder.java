package com.korinek.locate_sbm.ui.localization.localized_rooms;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.korinek.locate_sbm.R;
import com.korinek.locate_sbm.model.LocalizedRoom;
import com.korinek.locate_sbm.utils.RoomIconsHelper;

import java.util.Locale;

class NonLocalizedRoomViewHolder extends LocationSortedRoomAdapter.LocationSortedRoomViewHolder {
    private final ImageView roomIcon;
    private final TextView roomNameTextView;
    private final TextView roomLocationProbabilityTextView;

    public NonLocalizedRoomViewHolder(@NonNull View itemView) {
        super(itemView);
        roomIcon = itemView.findViewById(R.id.non_localized_room_icon);
        roomNameTextView = itemView.findViewById(R.id.non_localized_room_name);
        roomLocationProbabilityTextView = itemView.findViewById(R.id.non_localized_location_probability);
    }

    public void bind(LocalizedRoom localizedRoom) {
        roomIcon.setImageResource(RoomIconsHelper.getIconResId(localizedRoom.getIcon()));
        roomNameTextView.setText(localizedRoom.getName());
        roomLocationProbabilityTextView.setText(String.format(Locale.getDefault(),"%.0f %%", localizedRoom.getLocationProbability()));
    }
}
