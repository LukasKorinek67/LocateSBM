package com.korinek.locate_sbm.ui.building;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.TooltipCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.korinek.locate_sbm.R;
import com.korinek.locate_sbm.model.RoomWithWifiFingerprints;
import com.korinek.locate_sbm.utils.RoomIconsHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private final List<RoomWithWifiFingerprints> rooms = new ArrayList<>();
    private final RoomAdapter.RoomActionListener listener;

    public interface RoomActionListener {
        void onRoomClick(RoomWithWifiFingerprints room);
        void onRoomCalibrate(RoomWithWifiFingerprints room);
        void onRoomEdit(RoomWithWifiFingerprints room);
        void onRoomDelete(RoomWithWifiFingerprints room);
    }

    public RoomAdapter(RoomAdapter.RoomActionListener listener) {
        this.listener = listener;
    }

    public void updateRooms(List<RoomWithWifiFingerprints> rooms) {
        this.rooms.clear();
        this.rooms.addAll(rooms);
        notifyDataSetChanged();
    }

    public RoomWithWifiFingerprints getRoomAt(int position) {
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
        RoomWithWifiFingerprints room = rooms.get(position);
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
        private final ImageView isRoomCalibrated;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomIcon = itemView.findViewById(R.id.room_icon);
            roomNameTextView = itemView.findViewById(R.id.room_name);
            roomMenu = itemView.findViewById(R.id.room_menu);
            isRoomCalibrated = itemView.findViewById(R.id.is_room_calibrated);
        }

        public void bind(RoomWithWifiFingerprints room, RoomAdapter.RoomActionListener listener) {
            roomIcon.setImageResource(RoomIconsHelper.getIconResId(room.getIcon()));
            roomNameTextView.setText(room.getName());
            itemView.setOnClickListener(v -> listener.onRoomClick(room));
            roomMenu.setOnClickListener(v -> showPopupMenu(v, room, listener));
            isRoomCalibrated.setVisibility(room.getWifiFingerprints().isEmpty() ? View.VISIBLE : View.INVISIBLE);
            if(room.getWifiFingerprints().isEmpty()) {
                // Tooltip text on isRoomCalibrated image
                TooltipCompat.setTooltipText(isRoomCalibrated, itemView.getContext().getString(R.string.tooltip_room_not_calibrated));
                // show tooltip on click instead of long click
                isRoomCalibrated.setOnClickListener(View::performLongClick);
            }
            // Tooltip text on roomName - not visible when too long
            TooltipCompat.setTooltipText(roomNameTextView, roomNameTextView.getText());
            roomNameTextView.setOnClickListener(View::performLongClick);
        }

        private void showPopupMenu(View view, RoomWithWifiFingerprints room, RoomAdapter.RoomActionListener listener) {
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
