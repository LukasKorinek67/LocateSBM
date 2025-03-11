package com.korinek.locate_sbm.ui.localization.localized_rooms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.korinek.locate_sbm.R;
import com.korinek.locate_sbm.utils.RoomAttributesHelper;

import java.util.ArrayList;
import java.util.List;
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

        public RoomDataOverviewViewHolder(@NonNull View itemView) {
            super(itemView);
            dataIcon = itemView.findViewById(R.id.data_icon);
            dataValueTextView = itemView.findViewById(R.id.data_value_text_view);
        }

        public void bind(String key, Object value) {
            int icon = RoomAttributesHelper.getIconForAttribute(key);
            dataIcon.setImageResource(icon);
            dataValueTextView.setText(RoomAttributesHelper.getAttributeStringValue(value));
        }
    }
}
