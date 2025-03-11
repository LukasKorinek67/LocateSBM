package com.korinek.locate_sbm.ui.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.korinek.locate_sbm.R;
import com.korinek.locate_sbm.model.Room;
import com.korinek.locate_sbm.utils.RoomIconsHelper;

import java.util.List;

public class LoadedRoomEditAdapter extends RecyclerView.Adapter<LoadedRoomEditAdapter.LoadedRoomEditViewHolder> {
    private final List<Room> rooms;
    private final Context context;
    int[] icons;

    public LoadedRoomEditAdapter(Context context, List<Room> rooms) {
        this.context = context;
        this.rooms = rooms;
    }

    @NonNull
    @Override
    public LoadedRoomEditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_loaded_room_edit_item, parent, false);
        return new LoadedRoomEditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoadedRoomEditViewHolder holder, int position) {
        Room room = rooms.get(position);

        holder.roomName.setText(room.getName());
        icons = RoomIconsHelper.getIcons();

        ArrayAdapter<Integer> selectorAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_edit_loaded_room_icon_item, parent, false);
                }
                ImageView imageView = convertView.findViewById(R.id.edit_spinner_icon);
                imageView.setImageResource(icons[position]);
                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_edit_loaded_room_icon_item, parent, false);
                }
                ImageView imageView = convertView.findViewById(R.id.edit_spinner_icon);
                imageView.setImageResource(icons[position]);
                return convertView;
            }

            @Override
            public int getCount() {
                return icons.length;
            }
        };
        holder.editIconSpinner.setAdapter(selectorAdapter);

        int selectedIconResId = RoomIconsHelper.getIconResId(room.getIcon());
        int selectedIconIndex = -1;
        for (int i = 0; i < icons.length; i++) {
            if (icons[i] == selectedIconResId) {
                selectedIconIndex = i;
                break;
            }
        }
        if (selectedIconIndex != -1) {
            holder.editIconSpinner.setSelection(selectedIconIndex);
        }

        holder.editIconSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedIconResId = icons[position];
                String iconName = RoomIconsHelper.getIconName(selectedIconResId);
                room.setIcon(iconName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    static class LoadedRoomEditViewHolder extends RecyclerView.ViewHolder {
        Spinner editIconSpinner;
        TextView roomName;

        public LoadedRoomEditViewHolder(@NonNull View itemView) {
            super(itemView);
            editIconSpinner = itemView.findViewById(R.id.loaded_room_edit_icon_selector);
            roomName = itemView.findViewById(R.id.loaded_room_name);
        }
    }
}
