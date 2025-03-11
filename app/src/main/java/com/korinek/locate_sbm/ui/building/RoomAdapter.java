package com.korinek.locate_sbm.ui.building;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.korinek.locate_sbm.R;
import com.korinek.locate_sbm.model.Room;
import com.korinek.locate_sbm.utils.RoomIconsHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private final List<Room> rooms = new ArrayList<>();
    private final RoomAdapter.RoomActionListener listener;

    public interface RoomActionListener {
        void onRoomClick(Room room);
        void onRoomCalibrate(Room room);
        void onRoomEdit(Room room);
        void onRoomDelete(Room room);
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
        private final ImageView roomIcon;
        private final TextView roomNameTextView;
        private final ImageView roomMenu;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomIcon = itemView.findViewById(R.id.room_icon);
            roomNameTextView = itemView.findViewById(R.id.room_name);
            roomMenu = itemView.findViewById(R.id.room_menu);
        }

        public void bind(Room room, RoomAdapter.RoomActionListener listener) {
            roomIcon.setImageResource(RoomIconsHelper.getIconResId(room.getIcon()));
            roomNameTextView.setText(room.getName());
            itemView.setOnClickListener(v -> listener.onRoomClick(room));
            roomMenu.setOnClickListener(v -> showPopupMenu(v, room, listener));
        }

        private void showPopupMenu(View view, Room room, RoomAdapter.RoomActionListener listener) {
            PopupMenu popup = new PopupMenu(view.getContext(), view);
            popup.inflate(R.menu.room_menu);

            enableMenuIcons(popup);

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_calibrate) {
                    listener.onRoomCalibrate(room);
                    return true;
                } else if (item.getItemId() == R.id.menu_edit) {
                    listener.onRoomEdit(room);
                    return true;
                } else if (item.getItemId() == R.id.menu_delete) {
                    listener.onRoomDelete(room);
                    return true;
                }
                return false;
            });

            popup.show();
        }

        private void enableMenuIcons(PopupMenu popup) {
            try {
                Field field = popup.getClass().getDeclaredField("mPopup");
                field.setAccessible(true);
                Object menuPopupHelper = field.get(popup);
                Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                Method setForceShowIcon = classPopupHelper.getDeclaredMethod("setForceShowIcon", boolean.class);
                setForceShowIcon.setAccessible(true);
                setForceShowIcon.invoke(menuPopupHelper, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
