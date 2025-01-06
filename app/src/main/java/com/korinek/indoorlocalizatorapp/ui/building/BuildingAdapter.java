package com.korinek.indoorlocalizatorapp.ui.building;


import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.korinek.indoorlocalizatorapp.model.Building;
import com.korinek.indoorlocalizatorapp.R;

import java.util.List;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.BuildingViewHolder> {
    private List<Building> buildings;
    private OnBuildingClickListener listener;

    public interface OnBuildingClickListener {
        void onBuildingClick(Building building);
    }

    public BuildingAdapter(List<Building> buildings, OnBuildingClickListener listener) {
        this.buildings = buildings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BuildingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_building_item, parent, false);
        return new BuildingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BuildingViewHolder holder, int position) {
        Building building = buildings.get(position);
        holder.bind(building, listener);
    }

    @Override
    public int getItemCount() {
        return buildings.size();
    }

    static class BuildingViewHolder extends RecyclerView.ViewHolder {
        private View buildingColorView;
        private TextView buildingNameTextView;

        public BuildingViewHolder(@NonNull View itemView) {
            super(itemView);
            buildingNameTextView = itemView.findViewById(R.id.building_text_view);
            buildingColorView = itemView.findViewById(R.id.building_color_view);
        }

        public void bind(Building building, OnBuildingClickListener listener) {
            GradientDrawable drawable = (GradientDrawable) buildingColorView.getBackground();
            drawable.setColor(ContextCompat.getColor(itemView.getContext(), building.getColour()));
            buildingNameTextView.setText(building.getName());
            itemView.setOnClickListener(v -> listener.onBuildingClick(building));
        }
    }
}
