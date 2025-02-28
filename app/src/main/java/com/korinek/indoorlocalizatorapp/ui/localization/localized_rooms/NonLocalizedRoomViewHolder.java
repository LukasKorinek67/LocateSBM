package com.korinek.indoorlocalizatorapp.ui.localization.localized_rooms;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.korinek.indoorlocalizatorapp.R;
import com.korinek.indoorlocalizatorapp.model.Room;

class NonLocalizedRoomViewHolder extends LocationSortedRoomAdapter.LocationSortedRoomViewHolder {
    private final ImageView roomIcon;
    private final TextView roomNameTextView;

    public NonLocalizedRoomViewHolder(@NonNull View itemView) {
        super(itemView);
        roomIcon = itemView.findViewById(R.id.non_localized_room_icon);
        roomNameTextView = itemView.findViewById(R.id.non_localized_room_name);
    }

    public void bind(Room room) {
        roomIcon.setImageResource(room.getIcon());
        roomNameTextView.setText(room.getName());
    }
}
