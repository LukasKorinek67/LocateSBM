package com.korinek.indoorlocalizatorapp.ui.building;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.korinek.indoorlocalizatorapp.R;
import com.korinek.indoorlocalizatorapp.model.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private final List<Room> rooms = new ArrayList<>();
    private final RoomAdapter.RoomActionListener listener;

    public interface RoomActionListener {
        void onRoomClick(Room room);
        void onRoomSwiped(Room room);
    }

    public RoomAdapter(RoomAdapter.RoomActionListener listener) {
        this.listener = listener;
    }

    public void updateRooms(List<Room> rooms) {
        this.rooms.clear();
        this.rooms.addAll(rooms);
        notifyDataSetChanged();
    }

    public Room getRoomAt(int position) {
        return rooms.get(position);
    }

    @NonNull
    @Override
    public RoomAdapter.RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_room_item, parent, false);
        return new RoomAdapter.RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomAdapter.RoomViewHolder holder, int position) {
        Room room = rooms.get(position);
        holder.bind(room, listener);
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public RoomAdapter.RoomActionListener getListener() {
        return listener;
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        private final TextView roomNameTextView;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomNameTextView = itemView.findViewById(R.id.room_name);
        }

        public void bind(Room room, RoomAdapter.RoomActionListener listener) {
            roomNameTextView.setText(room.getName());
            itemView.setOnClickListener(v -> listener.onRoomClick(room));
        }
    }
}
