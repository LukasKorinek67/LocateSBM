package com.korinek.locate_sbm.ui.localization.localized_rooms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.korinek.locate_sbm.R;
import com.korinek.locate_sbm.model.LocalizedRoom;

import java.util.ArrayList;
import java.util.List;

public class LocationSortedRoomAdapter extends RecyclerView.Adapter<LocationSortedRoomAdapter.LocationSortedRoomViewHolder> {

    private final List<LocalizedRoom> rooms = new ArrayList<>();
    private final Fragment parentFragment;
    private int localizedIndex = 0;
    private static final int VIEW_TYPE_LOCALIZED = 0;
    private static final int VIEW_TYPE_NON_LOCALIZED = 1;

    public LocationSortedRoomAdapter(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    public void updateRooms(List<LocalizedRoom> rooms) {
        this.rooms.clear();
        this.rooms.addAll(rooms);
        notifyDataSetChanged();
    }

    public LocalizedRoom getRoomAt(int position) {
        return rooms.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == localizedIndex) ? VIEW_TYPE_LOCALIZED : VIEW_TYPE_NON_LOCALIZED;
    }

    @NonNull
    @Override
    public LocationSortedRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_LOCALIZED) {
            View view = inflater.inflate(R.layout.adapter_localized_room_item, parent, false);
            return new LocalizedRoomViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.adapter_non_localized_room_item, parent, false);
            return new NonLocalizedRoomViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull LocationSortedRoomViewHolder holder, int position) {
        LocalizedRoom room = rooms.get(position);

        holder.itemView.setOnClickListener(v -> {
            if (localizedIndex != holder.getAdapterPosition()) {
                localizedIndex = holder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });

        if (holder instanceof LocalizedRoomViewHolder) {
            ((LocalizedRoomViewHolder) holder).bind(room, parentFragment);
        } else if (holder instanceof NonLocalizedRoomViewHolder) {
            ((NonLocalizedRoomViewHolder) holder).bind(room);
        }
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public abstract static class LocationSortedRoomViewHolder extends RecyclerView.ViewHolder {
        public LocationSortedRoomViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
