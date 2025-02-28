package com.korinek.indoorlocalizatorapp.ui.localization.localized_rooms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.korinek.indoorlocalizatorapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RoomDataOverviewAdapter extends RecyclerView.Adapter<RoomDataOverviewAdapter.RoomDataOverviewViewHolder>{

    private Map<String, Object> attributes;

    public RoomDataOverviewAdapter(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @NonNull
    @Override
    public RoomDataOverviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_room_data_overview_item, parent, false);
        return new RoomDataOverviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomDataOverviewViewHolder holder, int position) {
        List<Map.Entry<String, Object>> entryList = new ArrayList<>(attributes.entrySet());
        Map.Entry<String, Object> entry = entryList.get(position);
        holder.bind(entry.getKey(), entry.getValue());
    }

    @Override
    public int getItemCount() {
        return attributes.size();
    }

    public void updateAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        notifyDataSetChanged();
    }

    static class RoomDataOverviewViewHolder extends RecyclerView.ViewHolder {
        private final ImageView dataIcon;
        private final TextView dataValueTextView;
        private final Map<String, Integer> ICON_MAP = new HashMap<>();

        public RoomDataOverviewViewHolder(@NonNull View itemView) {
            super(itemView);
            dataIcon = itemView.findViewById(R.id.data_icon);
            dataValueTextView = itemView.findViewById(R.id.data_value_text_view);
            fillMap();
        }

        private void fillMap() {
            ICON_MAP.put("temp", R.drawable.ic_temperature_celsius);
            ICON_MAP.put("rh", R.drawable.ic_humidity);
            ICON_MAP.put("co2", R.drawable.ic_co2);
            ICON_MAP.put("tempW", R.drawable.ic_temperature_set);
            ICON_MAP.put("valve", R.drawable.ic_valve);
            ICON_MAP.put("light1", R.drawable.ic_light);
            ICON_MAP.put("light2", R.drawable.ic_light);
            ICON_MAP.put("light3", R.drawable.ic_light);
            ICON_MAP.put("blinds", R.drawable.ic_blinds);
            ICON_MAP.put("ventilation", R.drawable.ic_ventilation);
        }

        public void bind(String key, Object value) {
            int icon = getDrawable(key);
            dataIcon.setImageResource(icon);

            if (value instanceof Integer) {
                dataValueTextView.setText(String.format(Locale.getDefault(),"%d", (Integer) value));
            } else if (value instanceof Double) {
                dataValueTextView.setText(String.format(Locale.getDefault(),"%.2f", (Double) value));
            } else {
                dataValueTextView.setText(value.toString());
            }
        }

        private int getDrawable(String key) {
            return ICON_MAP.get(key).intValue();
        }
    }
}
